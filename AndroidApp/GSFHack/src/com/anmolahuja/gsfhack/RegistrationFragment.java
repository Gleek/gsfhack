package com.anmolahuja.gsfhack;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntityBuilder;
import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;

public class RegistrationFragment extends Fragment
{

	private String name, email, mobile;
	private ProgressDialog pd;
	private String m_filePath = null;
	private static final int FILE_SELECT_CODE = 1234;

	private static final String LOG_TAG = "RegFragment";

	public RegistrationFragment()
	{
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState )
	{
		final View rootView = inflater.inflate( R.layout.registration_activity,
				container, false );
		final Spinner spinner = (Spinner) rootView
				.findViewById( R.id.spn_profilemethod );
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter
				.createFromResource( getActivity(), R.array.profile_options,
						android.R.layout.simple_spinner_item );
		adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		spinner.setAdapter( adapter );
		spinner.setSelection( 0 );

		( (Button) rootView.findViewById( R.id.btn_submit ) )
				.setOnClickListener( new OnClickListener()
				{

					@Override
					public void onClick( View v )
					{
						name = ( (EditText) rootView
								.findViewById( R.id.et_name ) ).getText()
								.toString();
						email = ( (EditText) rootView
								.findViewById( R.id.et_email ) ).getText()
								.toString();
						mobile = ( (EditText) rootView
								.findViewById( R.id.et_mobileno ) ).getText()
								.toString();

						if( name == null || name.isEmpty() || email == null
								|| email.isEmpty() || mobile == null
								|| mobile.isEmpty() )
						{
							Toast.makeText( getActivity(),
									"Please fill in all the fields",
									Toast.LENGTH_SHORT ).show();
							return;
						}
						switch( spinner.getSelectedItemPosition() )
						{
						case 0:
							// Social TODO
							break;
						case 1:
							// PDF
							// C-TODO hardcoding for testing
							m_filePath = "/sdcard/xkcd-24.jpeg";
							/*
							 * if (m_filePath == null || m_filePath.isEmpty()) {
							 * showFileChooser(); return; }
							 */
							break;
						case 2:
							// TODO show warning about call rates
							beginUpload( new RegistrationListener(){
								@Override
								public void onUploadFinished( final
										boolean registrationSuccessful )
								{
									getActivity().runOnUiThread( new Runnable()
									{
										@Override
										public void run()
										{
											if( !registrationSuccessful )
												return;
											try
											{
												Intent callIntent = new Intent(
														Intent.ACTION_CALL );
												callIntent.setData( Uri
														.parse( "tel:+911130715373" ) );
												startActivity( callIntent );
											}
											catch( ActivityNotFoundException e )
											{
												e.printStackTrace();
											}
										}} );
										}
									} );
							break;

						case 3:
							// pDF Pic Export
							AlertDialog.Builder b = new AlertDialog.Builder(
									getActivity() );
							b.setTitle( "Pick a source" )
									.setSingleChoiceItems(
											new String[] { "Camera", "Gallerr" },
											0,
											new DialogInterface.OnClickListener()
											{
												@Override
												public void onClick(
														DialogInterface dialog,
														int which )
												{
													Intent intent = new Intent();
													switch( which )
													{
													case 0:
														// camera
														// N-TODO
														break;
													case 1:
														// gallery
														// N-TODO
														break;
													}
													beginUpload( null );
													dialog.dismiss();
												}
											} )
									.show();
							return;
						}
						beginUpload( null );
					}
				} );
		return rootView;
	}

	private void showFileChooser()
	{
		// file picker code from:
		// ANMLINK[1]
		// http://stackoverflow.com/questions/7856959/android-file-chooser
		Intent intent = new Intent( Intent.ACTION_GET_CONTENT );
		intent.setType( "*/*" );
		intent.addCategory( Intent.CATEGORY_OPENABLE );

		try
		{
			startActivityForResult(
					Intent.createChooser( intent, "Select a File to Upload" ),
					FILE_SELECT_CODE );
		}
		catch( android.content.ActivityNotFoundException ex )
		{
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText( getActivity(), "Please install a File Manager.",
					Toast.LENGTH_SHORT ).show();
		}
	}

	@Override
	public void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		Log.v( LOG_TAG, "In on acitivyt reslt, rc: " + resultCode );
		if( resultCode != Activity.RESULT_OK )
			return;

		switch( resultCode )
		{
		case FILE_SELECT_CODE: // ANMLINK[1]
			// Get the Uri of the selected file
			Uri uri = data.getData();
			Log.d( "REgfragment", "File Uri: " + uri.toString() );
			// Get the path
			if( "content".equalsIgnoreCase( uri.getScheme() ) )
			{
				String[] projection = { "_data" };
				Cursor cursor = null;
				try
				{
					cursor = getActivity().getContentResolver().query( uri,
							projection, null, null, null );
					int column_index = cursor.getColumnIndexOrThrow( "_data" );
					if( cursor.moveToFirst() )
					{
						m_filePath = cursor.getString( column_index );
					}
				}
				catch( Exception e )
				{
					// Eat it
				}
			}
			else if( "file".equalsIgnoreCase( uri.getScheme() ) )
			{
				m_filePath = uri.getPath();
			}

			Log.v( LOG_TAG, "File Path: " + m_filePath );
			if( m_filePath == null || m_filePath.isEmpty() )
			{
				Toast.makeText( getActivity(), "Please select a file!",
						Toast.LENGTH_LONG ).show();
				return;
			}
			beginUpload( null );
			break;
		}
	}

	private void beginUpload( final RegistrationListener listener )
	{
		pd = new ProgressDialog( getActivity() );
		pd.setTitle( "Registering..." );
		pd.show();
		Thread t = new Thread( new Runnable()
		{
			@Override
			public void run()
			{
				boolean successful = false;
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(
						"http://107.161.27.22:5000/api/upload" );
				try
				{
					MultipartEntityBuilder builder = MultipartEntityBuilder
							.create();
					if( m_filePath != null )
					{
						File pdfFile = new File( m_filePath );
						Log.v( LOG_TAG, "Adding file: " + m_filePath
								+ "File exists: " + pdfFile.exists() );
						builder.addPart( "file", new FileBody( pdfFile ) );
					}
					httppost.setEntity( builder.build() );
					HttpResponse response = httpclient.execute( httppost );
					Log.e( "test", "SC:"
							+ response.getStatusLine().getStatusCode() );
					BufferedReader reader = new BufferedReader(
							new InputStreamReader( response.getEntity()
									.getContent(), "UTF-8" ) );
					String s = "", temp; // TODO use SB
					while( ( temp = reader.readLine() ) != null )
					{
						s += temp;
					}
					if( pd != null && pd.isShowing() )
					{
						pd.dismiss();
					}
					if( s.contains( "success" ) ) // hammad?
					{
						successful = true;
					}
					else
					{
						// TODO
					}
					Log.v( LOG_TAG, "Response: " + s );
				}
				catch( ClientProtocolException e )
				{
				}
				catch( IOException e )
				{
				}
				finally
				{
					if( listener != null )
						listener.onUploadFinished( successful );
				}
			}
		} );
		t.start();
	}
	private static interface RegistrationListener
	{
		public void onUploadFinished( boolean registrationSuccessful );
	}
}
