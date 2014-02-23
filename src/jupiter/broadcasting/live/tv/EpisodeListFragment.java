package jupiter.broadcasting.live.tv;

/*
 * Copyright (c) 2012 Shane Quigley
 *
 * This software is MIT licensed see link for details
 * http://www.opensource.org/licenses/MIT
 *
 * @author Shane Quigley
 * @hacked Adam Szabo
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Hashtable;
import java.util.List;

import jupiter.broadcasting.live.tv.parser.RssHandler;
import jupiter.broadcasting.live.tv.parser.SaxRssParser;

public class EpisodeListFragment extends Fragment {

    List<String> episodes;
    String afeed, vfeed;
    Hashtable<String, String[]> arssLinkTable;
    Hashtable<String, String[]> vrssLinkTable;
    ListView asyncResultView;
    static View v;
    
    ActionMode mMode;
    MenuBarFragment mFragment1;
    static String aurls[];
    static String vurls[];
    static String title;
    ArrayAdapter<String> adapter;
    boolean first;
    int opId;
    FragmentTransaction ft;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setProgressBarIndeterminateVisibility(true);

        v = inflater.inflate(R.layout.episodelist_fragment, null);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        ft = fm.beginTransaction();
        if (mFragment1 == null){
            mFragment1 = new MenuBarFragment();
            ft.add(mFragment1, "mf");
        }
        ft.commit();
        mFragment1.setHasOptionsMenu(true);
        mFragment1.setMenuVisibility(false);

        opId = 555;
        asyncResultView = (ListView) v.findViewById(R.id.episodelist);
        asyncResultView.setOnScrollListener(new EndlessScrollListener());
        asyncResultView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long d = id;
                aurls = arssLinkTable.get(parent.getAdapter().getItem(position));
                vurls = vrssLinkTable.get(parent.getAdapter().getItem(position));
                title = (String) parent.getAdapter().getItem(position);
                //mMode = getActivity().startSupportActionMode(new EpisodeActionMode());
                //actionmode replaced by menufragment, just show or hide

                if (mFragment1.isMenuVisible() && opId == position) {
                    mFragment1.setMenuVisibility(false);
                } else {
                    mFragment1.setMenuVisibility(true);
                    opId = position;
                }
            }
        });
        
        Bundle b = getArguments();
        afeed = b.getString("SHOW_AUDIO");
        vfeed = b.getString("SHOW_VIDEO");
        first = true;
        RSS_parse newparse = new RSS_parse();  //do networking in async task SDK>9
        newparse.execute(afeed, vfeed, "0");

        return v;
    }

    

    public class EndlessScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 5;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener() {

        }

        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            //visibleThreshold = (5<arssLinkTable.size() && arssLinkTable.size()>0) ? 5:arssLinkTable.size()-1;
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                // load the next page of shows using a background task
                //getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
                currentPage++;
                RSS_parse scrollparse = new RSS_parse();
                scrollparse.execute(afeed, vfeed, String.valueOf(currentPage));

                loading = true;

            }
        }
    }


    public class RSS_parse extends AsyncTask<String, Integer, List<String>> {

        @Override
        protected List<String> doInBackground(String... link) {

            int page = Integer.parseInt(link[2]);
            SaxRssParser aparser = new SaxRssParser();
            SaxRssParser vparser = new SaxRssParser();
            RssHandler acustomhandler = new RssHandler("title", "link", page);
            RssHandler vcustomhandler = new RssHandler("title", "link", page);
            aparser.setRssHadler(acustomhandler);
            vparser.setRssHadler(vcustomhandler);
            if (first) {
                arssLinkTable = aparser.parse(link[0]);
                vrssLinkTable = vparser.parse(link[1]);

            } else {
                arssLinkTable.putAll(aparser.parse(link[0]));
                vrssLinkTable.putAll(vparser.parse(link[1]));
            }
            episodes = vparser.getTitles();

            return episodes;
        }

        @Override
        protected void onPostExecute(List<String> args) {
            if (first) {
                adapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, args);


                asyncResultView.setAdapter(adapter);
                getActivity().setProgressBarIndeterminateVisibility(false);
                first = false;
            } else {
                for (int i = 0; i < args.size(); i++) {
                    adapter.add(args.get(i));
                }
                adapter.notifyDataSetChanged();
            }

        }
    }

    public static class MenuBarFragment extends Fragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            MenuItemCompat.setShowAsAction(menu.add(R.string.audio), MenuItem.SHOW_AS_ACTION_IF_ROOM);
            MenuItemCompat.setShowAsAction(menu.add(R.string.video), MenuItem.SHOW_AS_ACTION_IF_ROOM);
            MenuItemCompat.setShowAsAction(menu.add(R.string.notes), MenuItem.SHOW_AS_ACTION_IF_ROOM);
            super.onCreateOptionsMenu(menu, inflater);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            setMenuVisibility(false);
            //if wifi connected
            ConnectivityManager connectivity = (ConnectivityManager) getActivity()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo wifiInfo = connectivity
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (item.getTitle().equals(getString(R.string.notes))) {

                String link = aurls[0];
                Intent i = new Intent(v.getContext(), ShowNotesView.class);
                i.putExtra("link", link);
                i.putExtra("name", title);
                startActivity(i);
                return true;
            }
            if (item.getTitle().equals(getString(R.string.video))) {
                if (wifiInfo == null || wifiInfo.getState() != NetworkInfo.State.CONNECTED) {
                    AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
                    myAlertDialog.setTitle(R.string.alert);
                    myAlertDialog.setMessage(R.string.areyousure);
                    myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            // start videostreaming if the user agrees
                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(vurls[1]));
                            i.setDataAndType(Uri.parse(vurls[1]), "video/mp4");
                            startActivity(i);
                        }
                    });

                    myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });

                    myAlertDialog.show();
                } else {
                    Intent j = new Intent(Intent.ACTION_VIEW, Uri.parse(vurls[1]));
                    j.setDataAndType(Uri.parse(vurls[1]), "video/mp4");
                    startActivity(j);
                }
                return true;
            }
            if (item.getTitle().equals(getString(R.string.audio))) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(aurls[1]));
                i.setDataAndType(Uri.parse(aurls[1]), "audio/mp3");
                startActivity(i);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
