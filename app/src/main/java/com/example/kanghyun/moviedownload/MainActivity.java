package com.example.kanghyun.moviedownload;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Base64;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.MimeTypeMap;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            Bundle args = null;
            switch (position) {
                case 0:
                    fragment = new mainFragment();
                    args = new Bundle();
                    break;
                case 1:
                    fragment = new mainFragment2();
                    args = new Bundle();
                    break;
                case 2:
                    fragment = new mainFragment3();
                    args = new Bundle();
                    break;
            }
            return fragment;
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            // return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "FTP Torrent";
                case 1:
                    return "Download";
                case 2:
                    return "Find";
            }
            return null;
        }
    }

    public static class mainFragment extends Fragment implements View.OnClickListener {


        Button btSearch;
        Button btSend;
        TextView txPath;
        ListView Filelist;

        List myList;
        File file;
        List<String> selectedItems = new ArrayList<>();

        String rootSD;
        AlertDialog.Builder ab1;


        public void setListViewFilenm() {

            rootSD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            file = new File(rootSD);
            System.out.println("rootSD = " + rootSD);
            //txPath.setText(rootSD+ "/Download");
            txPath.setText(rootSD);
            myList = new ArrayList();

            System.out.println("lfile = " + file);

            if (file.listFiles() != null) {

                FileFilter fFilter = new FileFilter();

                File list[] = file.listFiles(fFilter);

                System.out.println("list.length = " + list.length);
                if (list.length > 0) {
                    for (int i = 0; i < list.length; i++) {
                        myList.add(list[i].getName());

                    }
                }
            }

            if (!myList.isEmpty()) {

                ArrayAdapter ap = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_multiple_choice, myList);

                Filelist.setAdapter(ap);
                Filelist.deferNotifyDataSetChanged();

            }
        }

        public void fileUpload() {
            FTPClient con;
            con = new FTPClient();
            String ftpID = "pi";
            String ftpPW = "raspberry";
            boolean uploadResult = false;
            String URL = null;
            int ftpPort = 0;
            ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connMgr.getActiveNetworkInfo();
            if ((info != null) && (info.isAvailable() == true)) {
                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    //WIFI 연결
                    System.out.println("WIFI연결상태");
                    URL = "192.168.25.21";
                    ftpPort = 21;
                } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    //Data 연결
                    System.out.println("MOBILE연결상태");
                    URL = "116.120.176.234";
                    ftpPort = 1024;
                }
            }


            SparseBooleanArray checkedItemPositions = Filelist.getCheckedItemPositions();
            for (int i = 0; i < checkedItemPositions.size(); i++) {
                int pos = checkedItemPositions.keyAt(i);

                if (checkedItemPositions.valueAt(i)) {
                    selectedItems.add(Filelist.getItemAtPosition(pos).toString());

                }
            }

            try {
                con.setControlEncoding("utf-8");
                con.connect(URL, ftpPort);
                int reply = con.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                    try {
                        throw new Exception("ftp connection refused");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    System.out.println("연결성공");

                }


                System.out.println("logon 시도");

                con.setSoTimeout(1000 * 3);

                if (con.login(ftpID, ftpPW)) {
                    System.out.println("Success");

                    con.enterLocalPassiveMode();
                    con.setBufferSize(1024 * 1024);
                    con.setFileType(FTP.BINARY_FILE_TYPE);

                    for (int i = 0; i < selectedItems.size(); i++) {

                        File f = new File(rootSD + "/" + selectedItems.get(i).toString());
                        System.out.println(selectedItems.get(i).toString());
                        FileInputStream in = new FileInputStream(f);
                        uploadResult = con.storeFile("/home/exHDD/seeds/" + selectedItems.get(i).toString(), in);


                    }

                    if (uploadResult) {
                        System.out.println("Success");

                        ab1.setMessage("업로드성공");
                        ab1.show();
                    } else {
                        System.out.println("fail");

                        ab1.setMessage("업로드Fail");
                        ab1.show();

                    }


                    con.logout();
                    con.disconnect();
                    selectedItems.clear();


                }

            } catch (IOException e) {
                e.printStackTrace();
                selectedItems.clear();
            }


        }

        public mainFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            btSearch = (Button) rootView.findViewById(R.id.button);
            btSend = (Button) rootView.findViewById(R.id.button2);
            txPath = (TextView) rootView.findViewById(R.id.textView2);
            Filelist = (ListView) rootView.findViewById(R.id.ListView);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            ab1 = new AlertDialog.Builder(getActivity());
            ab1.setPositiveButton("확인", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            btSearch.setOnClickListener(this);
            btSend.setOnClickListener(this);

            return rootView;
        }

        @Override
        public void onClick(View v) {


            if (v.getId() == R.id.button) {
                System.out.println("R.id.button");
                selectedItems.clear();
                setListViewFilenm();
            } else if (v.getId() == R.id.button2) {
                fileUpload();
            }

        }
    }

    public static class mainFragment2 extends Fragment {

        private WebView mWebView;
        String URL = null;
        int ftpPort = 0;


        public mainFragment2() {


        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
            ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);


            NetworkInfo info = connMgr.getActiveNetworkInfo();
            if ((info != null) && (info.isAvailable() == true)) {
                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    //WIFI 연결
                    System.out.println("WIFI연결상태");
                    URL = "192.168.25.21";
                    ftpPort = 1234;
                } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    //Data 연결
                    System.out.println("MOBILE연결상태");
                    URL = "116.120.176.234";
                    ftpPort = 1023;
                }
            }
            mWebView = (WebView) rootView.findViewById(R.id.webview);
            System.out.println("http://" + URL + ":" + ftpPort + "/transmission/web/");

/*
            mWebView.setWebViewClient(new WebViewClientClass(){
                public void onPageFinished(WebView view, String url) {
                    String user="pi";
                    String pwd="raspberry";
                    view.loadUrl("javascript:document.getElementById('username').value = '"+user+"';document.getElementById('password').value='"+pwd+"';");
                }
            });
            mWebView.loadUrl("http://" + URL + ":" + ftpPort + "/transmission/web/");
            */

            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.setWebViewClient(new WebViewClient() {
                /*   public void onPageFinished(WebView view, String url) {
                       String user="pi";
                       String pwd="raspberry";
                       view.loadUrl("javascript:document.getElementById('username').value = '"+user+"';document.getElementById('password').value='"+pwd+"';");
                   }*/
                @Override
                public void onReceivedHttpAuthRequest(WebView view, final HttpAuthHandler handler, final String host, final String realm) {

                    String userName = "pi";
                    String userPass = "raspberry";

                    if (handler.useHttpAuthUsernamePassword() && view != null) {
                        String[] haup = view.getHttpAuthUsernamePassword(host, realm);

                        if (haup != null && haup.length == 2) {
                            userName = haup[0];
                            userPass = haup[1];
                        }
                    }

                    if (userName != null && userPass != null) {
                        handler.proceed(userName, userPass);
                    }

                }
            });
            mWebView.loadUrl("http://" + URL + ":" + ftpPort + "/transmission/web/");


            return rootView;
        }


    }

    public static class mainFragment3 extends Fragment {

        WebView mWebView;

        public mainFragment3() {


        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main3, container, false);
            mWebView = (WebView) rootView.findViewById(R.id.webview);
            // mWebView.loadUrl("https://m.torrentkim5.net/");
            mWebView.loadUrl("https://torrentkim5.net/");

            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
          //  mWebView.getSettings().setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);

            webSettings.setAllowFileAccess(true);
            webSettings.setAllowContentAccess(true);
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);





            // This will handle downloading. It requires Gingerbread, though
            final DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

            // This is where downloaded files will be written, using the package name isn't required
            // but it's a good way to communicate who owns the directory
            final File destinationDir = new File (Environment.getExternalStorageDirectory(), getActivity().getPackageName());
            if (!destinationDir.exists()) {
                destinationDir.mkdir(); // Don't forget to make the directory if it's not there
            }
            mWebView.setWebViewClient(new WebViewClient() {




                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {


              /*      boolean shouldOverride = false;
                    System.out.println("shouldOverrideUrlLoading");

                    // We only want to handle requests for mp3 files, everything else the webview
                    // can handle normally


                 //   Toast.makeText(getActivity(), url, Toast.LENGTH_LONG).show();
                    String cookie = CookieManager.getInstance().getCookie(url);
                    System.out.println("cookie = " + cookie);


                    if(url.contains("no=")) {

                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


                        request.addRequestHeader("Cookie", cookie);
                        manager.enqueue(request);
                    }else {
                        view.loadUrl(url);
                    }
                    return true;
*/

                    boolean shouldOverride = false;
                    // We only want to handle requests for mp3 files, everything else the webview
                    // can handle normally
                    if (url.contains("no=")) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                    }else {
                        view.loadUrl(url);
                    }

                    return shouldOverride;




/*
                    if (url.endsWith(".torrent")) {
                        shouldOverride = true;
                        Uri source = Uri.parse(url);

                        // Make a new request pointing to the mp3 url
                        DownloadManager.Request request = new DownloadManager.Request(source);
                        // Use the same file name for the destination
                        File destinationFile = new File(destinationDir, source.getLastPathSegment());
                        request.setDestinationUri(Uri.fromFile(destinationFile));
                        // Add it to the manager
                        manager.enqueue(request);
                    }
*/
                    //return super.shouldOverrideUrlLoading(view, url);
                }


                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    handler.proceed(); // Ignore SSL certificate errors
                }

                @Override
                public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                    // 키를 오버로딩한것인데 주로 웹페이지를 뒤,앞 등으로 이동하게 한다.
                    // 왼쪽키를 누르게 되면 뒤로, 오른쪽 키는 앞으로 가게 한다.
                    System.out.println("shouldOverrideKeyEvent");
                    int keyCode = event.getKeyCode();
                    if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT) && mWebView.canGoBack()) {
                        mWebView.goBack();
                        return true;
                    } else if ((keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) && mWebView.canGoForward()) {
                        mWebView.goBack();
                        return true;
                    }

                    return super.shouldOverrideKeyEvent(view, event);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    // Removes Progress Bar

                }


            });

            mWebView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    //This is the filter
                    if (event.getAction()!=KeyEvent.ACTION_DOWN)
                        return true;


                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (mWebView.canGoBack()) {
                            mWebView.goBack();
                        } else {
                            (getActivity()).onBackPressed();
                        }

                        return true;
                    }

                    return false;
                }
            });





            mWebView.setDownloadListener(new DownloadListener() {

                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                    System.out.println("onDownloadStart");

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    /*
                    Toast.makeText(getActivity().getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();

                    try {
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.setMimeType(mimeType);
                        request.addRequestHeader("User-Agent", userAgent);
                        request.setDescription("Downloading file");
                        String fileName = contentDisposition.replace("inline; filename=", "");
                        fileName = fileName.replaceAll("\"", "");
                        request.setTitle(fileName);
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                        DownloadManager dm = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                        dm.enqueue(request);
                        Toast.makeText(getActivity().getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {

                        if (ContextCompat.checkSelfPermission(getActivity(),
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                Toast.makeText(getActivity().getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        110);
                            } else {
                                Toast.makeText(getActivity().getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        110);
                            }
                        }
                    }
                    */
                    //for downloading directly through download manager
                    /*
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "download");
                        DownloadManager dm = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                        dm.enqueue(request);
                        */
                }
            });


            return rootView;

        }


        public static class WebViewClientClass extends WebViewClient {
            String username;
            String password;

            @Override
            public void onReceivedHttpAuthRequest(WebView view,
                                                  HttpAuthHandler handler, String host, String realm) {
                System.out.println("onReceivedHttpAuthRequest");
                handler.proceed("pi", "raspberry");
            }

            public void setBasicAuthentication(String username, String password) {
                this.username = "pi";
                this.password = "raspberry";
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                System.out.println("doUpdateVisitedHistory");
                super.doUpdateVisitedHistory(view, url, isReload);
            }

        }
    }
}

