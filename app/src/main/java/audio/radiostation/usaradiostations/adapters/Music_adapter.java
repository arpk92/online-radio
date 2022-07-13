package audio.radiostation.usaradiostations.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import audio.radiostation.usaradiostations.Main;
import audio.radiostation.usaradiostations.R;
import audio.radiostation.usaradiostations.constant.Constant;

import java.io.File;
import java.util.ArrayList;

public class Music_adapter extends RecyclerView.Adapter<Music_adapter.Myvieholder> {
    private ArrayList<File> mPeopleList ;
    private Context mContext;
    int selectedPosition = -1;
    public Music_adapter(Context context, ArrayList<File> billing_models) {
        this.mContext = context;
        this.mPeopleList = billing_models;
    }

    @Override
    public Music_adapter.Myvieholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext() );
        View v = inflater.inflate( R.layout.music_list, parent, false );
        Music_adapter.Myvieholder vh = new Music_adapter.Myvieholder( v );
        return vh;

    }

    @Override
    public void onBindViewHolder(final Music_adapter.Myvieholder holder, final int position) {
        holder.item_name.setText( mPeopleList.get( position ).getName() );

//        holder.item_name.setText(mPeopleList.get( position ).getBill_item_name());
        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("clickedsuc","sucessful");
                Constant.station_playing =  mPeopleList.get( position ).getName() ;
                Constant.imageurl = "";
                Constant.streamurl =  mPeopleList.get( position ).getAbsolutePath();
                Constant.position = position;
                ((Main)mContext).setrecdata();
                ((Main)mContext).play();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((Main)mContext).setdata(mPeopleList.get( position ).getAbsolutePath());
                        ((Main)mContext).timer();
                    }
                }, 300);


            }
        } );


        holder.menu.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final PopupMenu popup = new PopupMenu(mContext, holder.menu);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.menuss, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.play:

                                holder.itemView.callOnClick();
                                popup.dismiss();
                                break;
                                case R.id.delete:

                                    String file =mPeopleList.get( position ).getAbsolutePath();
                                    File mFile = new File(file);
                                    if (mFile.isFile()) {
                                        mFile.delete();

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                                            final Intent scanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE );
                                            MediaScannerConnection.scanFile( mContext, new String[]{file}, null, new MediaScannerConnection.OnScanCompletedListener() {

                                                public void onScanCompleted(String path, Uri uri) {
                                                    Log.i( "ExternalStorage", "Scanned " + path + ":" );
                                                    Log.i( "ExternalStorage", "-> uri=" + uri );
                                                }
                                            } );
                                        } else {
                                            final Intent intent = new Intent( Intent.ACTION_MEDIA_MOUNTED, Uri
                                                    .parse( file )
                                            );
                                            mContext.sendBroadcast ( intent );
                                        }

                                        mPeopleList.remove( position );
                                        notifyItemChanged( position );
                                        notifyDataSetChanged();
                                        ((Main)mContext).customtoast("delete success");

                                        Toast.makeText( mContext, "delete success", Toast.LENGTH_SHORT ).show();
                                    }
                                    else
                                    {
                                        ((Main)mContext).customtoast("already deleted");

                                        Toast.makeText( mContext, "already deleted", Toast.LENGTH_SHORT ).show();
                                    }
                                    popup.dismiss();
                                break;
                                case R.id.send:
                                    String sharePath =mPeopleList.get( position ).getAbsolutePath();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        Uri uri = Uri.parse( sharePath );
                                        Intent share = new Intent( Intent.ACTION_SEND );
                                        share.setType( ".mp3/*" );
                                        share.putExtra( Intent.EXTRA_STREAM, uri );
                                        share.addFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION );
                                        mContext.startActivity( Intent.createChooser( share, "Share Sound File" ) );
                                    } else {
                                        Intent emailIntent = new Intent( Intent.ACTION_SEND );
                                        emailIntent.setType( "text/plain" );
                                        emailIntent.putExtra( Intent.EXTRA_EMAIL, new String[]{"email@example.com"} );
                                        emailIntent.putExtra( Intent.EXTRA_SUBJECT, "subject here" );
                                        File file1 = new File( sharePath );
                                        if (!file1.exists() || !file1.canRead()) {

                                        }
                                        Uri uri = Uri.fromFile( file1 );
                                        emailIntent.setType( "audio/*" );
                                        emailIntent.putExtra( Intent.EXTRA_STREAM, uri );
                                        emailIntent.addFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION );
                                        emailIntent.putExtra( Intent.EXTRA_STREAM, uri );
                                        mContext.startActivity( Intent.createChooser( emailIntent, "Share File by" ) );
                                    }
                                    popup.dismiss();
                                break;
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }

        } );
    }


    @Override
    public int getItemCount() {
        return mPeopleList.size();
    }

    public class Myvieholder extends RecyclerView.ViewHolder {
        public TextView item_name;
        public TextView price;
        public View layout;
        ImageView menu,image;
        public Myvieholder(View itemView) {
            super( itemView );
            layout = itemView;
            item_name = (TextView) itemView.findViewById(R.id.tital);
            menu = itemView.findViewById(R.id.menu);
            image = itemView.findViewById(R.id.image);

        }

    }


}