package com.iven.feedreader.mainact;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.LeadingMarginSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iven.feedreader.R;
import com.iven.feedreader.domparser.RSSFeed;
import com.iven.feedreader.domparser.RSSItem;
import com.iven.feedreader.utils.ArticleUtils;
import com.iven.feedreader.utils.GlideUtils;
import com.iven.feedreader.utils.Preferences;

import org.jsoup.Jsoup;

public class ArticleFragment extends Fragment {

    //feed
    RSSFeed fFeed;
    RSSItem feedItem;
    String feedDescription;
    String feedCompleteDescription;
    String datDescription;
    String imageLink;
    String imageLink2;
    String feedLink;
    String feedTitle;
    String feedDate;
    String datDescriptionFirstformat;
    String datDescriptionSecondformat;
    String datDescriptionThirdformat;

    //views
    ViewGroup rootView;
    TextView title;
    TextView subtitle;
    ImageButton button_continue_reading;
    ImageButton button_share;
    ImageButton button_back;
    ImageView imageView;
    LinearLayout article_default_linearLayout;
    TextView articletext;

    //Spannable
    Spannable titleSpan;
    Spannable subtitleSpan;
    Spannable storySpan;
    LeadingMarginSpan leadingMarginSpan;

    float size;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //position
        int fPos;

        //initialize the feed (i.e. get all the data)
        fFeed = (RSSFeed) getArguments().getSerializable("feed");
        fPos = getArguments().getInt("pos");
        feedItem = fFeed.getItem(fPos);

        //get the content
        feedCompleteDescription = feedItem.getCompleteDescription();
        feedDescription = feedItem.getDescription();
        imageLink = feedItem.getImage();
        imageLink2 = feedItem.getImage2();
        feedLink = feedItem.getLink();
        feedTitle = feedItem.getTitle();
        feedDate = feedItem.getDate();
        datDescription = feedItem.getDescription();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //set the view
        rootView = (ViewGroup) inflater
                .inflate(R.layout.article_fragment, container, false);

        //article title and subtitle
        //title
        title = (TextView) rootView.findViewById(R.id.title);

        //subtitle
        subtitle = (TextView) rootView.findViewById(R.id.subtitle);

        //action Buttons

        //read more button
        button_continue_reading = (ImageButton) rootView.findViewById(R.id.button_continue);

        //this is the method to handle the continue reading button click
        View.OnClickListener listener_forward = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Preferences.WebViewEnabled(getContext())) {
                    ArticleUtils.openFeedPage(getActivity(), feedLink);
                } else {
                    ArticleUtils.continueReading(getActivity(), feedLink);
                }
            }
        };

        //set Read more listener
        button_continue_reading.setOnClickListener(listener_forward);

        if (Preferences.WebViewEnabled(getContext())) {

            //this is the method to handle the continue reading long button click
            View.OnLongClickListener listener_external = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    //open the url using external browser if webview is enabled
                    ArticleUtils.continueReading(getActivity(), feedLink);

                    return true;
                }
            };

            //set the long click listener
            button_continue_reading.setOnLongClickListener(listener_external);
        }

        //share button
        button_share = (ImageButton) rootView.findViewById(R.id.button_share);

        //this is the method to handle the share button click
        View.OnClickListener listener_share = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArticleUtils.share(getActivity(), feedLink, feedTitle);

            }
        };

        //set Share listener
        button_share.setOnClickListener(listener_share);

        //back button
        button_back = (ImageButton) rootView.findViewById(R.id.button_back);

        //this is the click listener to provide back navigation
        View.OnClickListener listener_back = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
                ArticleUtils.goBack(appCompatActivity);

            }
        };

        //set back button on click listener
        button_back.setOnClickListener(listener_back);

        titleSpan = new SpannableString(feedTitle.toUpperCase());

        titleSpan.setSpan(new StyleSpan(Typeface.BOLD), 0, titleSpan.length(), 0);

        //dynamically set title and subtitle according to the feed data
        //set title of the article
        title.setText(titleSpan);

        subtitleSpan = new SpannableString(feedDate);

        subtitleSpan.setSpan(new StyleSpan(Typeface.BOLD), 0, subtitleSpan.length(), 0);

        //set the date of the article to subtitle
        subtitle.setText(subtitleSpan);

        //set the articles text size from preferences

        //get the chosen article's text size from preferences
        size = Preferences.resolveTextSizeResId(getContext());

        //set it in SP unit
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, size + 4);
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, size - 5);

        //if the preference is enabled remove the imageview from the linear layout

        //ImageView
        imageView = (ImageView) rootView.findViewById(R.id.img);

        //initialize the article view linear layout
        article_default_linearLayout = (LinearLayout) rootView.findViewById(R.id.article_linearlayout);

        if (Preferences.imagesRemoved(getContext())) {

            article_default_linearLayout.removeView(imageView);

        }

        //else, load the image
        //if getImage() method fails (i.e when img is in content:encoded) load image2
        else if (imageLink.isEmpty()) {

            GlideUtils.loadImage(getActivity(), imageLink2, imageView);

            //else use image
        } else {

            GlideUtils.loadImage(getActivity(), imageLink, imageView);

        }

        //we can open the image on web browser on long click on the image
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
                                             public boolean onLongClick(View v) {

                                                 final Intent intent;

                                                 //open imageview links
                                                 if (imageLink.isEmpty()) {

                                                     intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageLink2));

                                                 }

                                                 //else use image
                                                 else {
                                                     intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageLink));

                                                 }

                                                 //open the image
                                                 ArticleUtils.openImageLink(getActivity(), intent);


                                                 return true;
                                             }
                                         }
        );

        //set the article's content

        //use complete description by default, but sometimes the method returns null
        if (feedCompleteDescription.contains("no desc")) {
            datDescription = Jsoup.parse(feedDescription).text().replace("Continua a leggere...", "");

            // else, use complete description
        } else {
            datDescription = Jsoup.parse(feedCompleteDescription).text().replace("Continua a leggere...", "");

        }

        //replace some items since this is a simple TextView
        datDescriptionFirstformat = datDescription.replace("Continue reading...", "");

        datDescriptionSecondformat = datDescriptionFirstformat.replace("Read More", "");

        datDescriptionThirdformat = datDescriptionSecondformat.replace("(read more)", "");

        //load the article inside a TextView
        articletext = (TextView) rootView.findViewById(R.id.webv);

        storySpan = new SpannableString(datDescriptionThirdformat);

        storySpan.setSpan(new StyleSpan(Typeface.ITALIC), 0, storySpan.length(), 0);

        leadingMarginSpan = new android.text.style.LeadingMarginSpan.Standard(10);

        storySpan.setSpan(leadingMarginSpan, 0, storySpan.length(), 0);

        //set the articles text
        articletext.setText(storySpan);

        //set the article text size according to preferences
        articletext.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);

        return rootView;

    }
}