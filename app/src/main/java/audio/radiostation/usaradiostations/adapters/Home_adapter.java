package audio.radiostation.usaradiostations.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bumptech.glide.Glide;

import audio.radiostation.usaradiostations.AdsUtilities;
import audio.radiostation.usaradiostations.Main;
import audio.radiostation.usaradiostations.R;
import audio.radiostation.usaradiostations.clases.Favorite_save_Prefrence;
import audio.radiostation.usaradiostations.constant.Constant;
import audio.radiostation.usaradiostations.fragments.Home_fragment;
import audio.radiostation.usaradiostations.model_class.Model_class;

import java.util.ArrayList;

public class Home_adapter extends RecyclerView.Adapter<Home_adapter.MyviewHolder> {
    ArrayList<Model_class> mylist = new ArrayList();
    ArrayList<Model_class> listmain = new ArrayList();
    private Activity mActivity;
    private Context mContext;

    int count =1;
    Context context;

    public Home_adapter(FragmentActivity fragmentActivity, ArrayList<Model_class> mylist) {
        context = fragmentActivity;
        this.mylist = mylist;
        this.listmain = mylist;
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.homefragment, parent, false);

        return new Home_adapter.MyviewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final Home_adapter.MyviewHolder holder, final int i) {
        holder.title.setText(mylist.get(i).getStation_name());
        Glide.with(context).load(mylist.get(i).getStation_image()).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (count==Constant.count)
                {

                    Constant.station_playing = mylist.get(i).getStation_name();
                    Constant.imageurl = mylist.get(i).getStation_image();
                    Constant.streamurl = mylist.get(i).getStation_url();




                    Constant.position = i;
                    Constant.Model_fav = mylist.get(i);
                    ((Main) context).setmaindata();
                    ((Main) context).play();
                    ((Main) context).addtorecent(mylist.get(i));
                    ((Main) context).check_favorite(mylist.get(i));
                    count=1;
                    AdsUtilities.CLICK_COUNT(mContext).loadFullScreenAd(mActivity);
                } else {
                    Constant.station_playing = mylist.get(i).getStation_name();
                    Constant.imageurl = mylist.get(i).getStation_image();
                    Constant.streamurl = mylist.get(i).getStation_url();
                    Constant.position = i;
                    Constant.Model_fav = mylist.get(i);
                    ((Main) context).setmaindata();
                    ((Main) context).play();
                    ((Main) context).addtorecent(mylist.get(i));
                    ((Main) context).check_favorite(mylist.get(i));
                    count++;
                }




            }
        });

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup;
                if (Favorite_save_Prefrence.getInstance(context).isFavorite(mylist.get(i))) {
                    popup = new PopupMenu(context, holder.menu);
                    popup.inflate(R.menu.menuadap2);
                } else {
                    popup = new PopupMenu(context, holder.menu);
                    popup.inflate(R.menu.menuadap);
                }


                //inflating menu from xml resource
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.play:
                                holder.itemView.callOnClick();
                                break;
                            case R.id.add_fav:
                                if (Favorite_save_Prefrence.getInstance(context).isFavorite(mylist.get(i))) {
                                    Favorite_save_Prefrence.getInstance(context).removeFavorite(context, mylist.get(i));

                                    ((Main)context).customtoast("Removed from favorite");

//                                    Toast.makeText(context, "Removed from favorite", Toast.LENGTH_SHORT).show();
                                } else {

                                    ((Main)context).customtoast("Added to favorites");

                                    Favorite_save_Prefrence.getInstance(context).addFavorite(context, mylist.get(i));
//                                    Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();


            }
        });

    }

    @Override
    public int getItemCount() {
        return mylist.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        ImageView image, menu;
        TextView title;

        public MyviewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            menu = itemView.findViewById(R.id.menu);
        }
    }


    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mylist = listmain;
                } else {
                    ArrayList<Model_class> filteredList = new ArrayList<>();
                    for (Model_class androidVersion : listmain) {
                        if (androidVersion.getStation_name().toLowerCase().contains(charString)) {
                            filteredList.add(androidVersion);
                        }
                    }
                    mylist = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mylist;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mylist = (ArrayList<Model_class>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }

}
