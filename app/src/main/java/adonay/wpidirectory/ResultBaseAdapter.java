package adonay.wpidirectory;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.LinkedList;

class ResultBaseAdapter extends BaseAdapter {

    private LinkedList<String> results;
    private LayoutInflater inflater;
    private SearchResultActivity resultActivity;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    ResultBaseAdapter(Context context, LinkedList<String> results) {
        this.results = results;
        resultActivity = (SearchResultActivity) context;
        inflater = LayoutInflater.from(context);

        // image loader setup
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.empty_url)
                .showImageOnFail(R.drawable.loading_failed)
                .showImageOnLoading(R.drawable.loading).build();
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public String getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        String currentContent = results.get(position);

        if(convertView == null){
            /*if(hasAttribute("Picture", currentContent)){
                convertView = inflater.inflate(R.layout.result_list_item_with_image, parent, false);
                holder = new Holder(convertView, this.resultActivity);
                convertView.setTag(holder);
            }else{*/
                convertView = inflater.inflate(R.layout.result_list_item, parent, false);
                holder = new Holder(convertView, this.resultActivity);
                convertView.setTag(holder);
            /*}*/
        }else{
            holder = (Holder) convertView.getTag();
        }

        holder.position = position;

        holder.resultName = (TextView) convertView.findViewById(R.id.result_name);
        if (hasAttribute("no results from this search", currentContent)) {
            holder.resultName.setText(R.string.invalid_search);
        }else if(hasAttribute("There were too many results", currentContent)) {
            holder.resultName.setText(R.string.be_more_specific);
        }else if(hasAttribute("There was no name", currentContent)){
            holder.resultName.setText(R.string.enter_data);
        }else if(hasAttribute("No name was found", currentContent)){
            holder.resultName.setText(R.string.no_name);
        }else{
            holder.resultName.setText(getName(currentContent).substring(1));
        }
        holder.resultDescription = (TextView) convertView.findViewById(R.id.result_description);

        String description = "";

        if (hasAttribute("Title", currentContent)) {
            description+= "Title: " + getTitle(currentContent) + "\n";
        }if (hasAttribute("Department", currentContent)) {
            description+= "Department: " + getDepartment(currentContent) + "\n";
        }if (hasAttribute("Email", currentContent)) {
            description+= "Email: " + getEmail(currentContent) + "\n";
        }if (hasAttribute("Advisor", currentContent)) {
            description+= "Advisor: " + getAdvisor(currentContent) + "\n";
        }if (hasAttribute("Major", currentContent)) {
            description+= "Major: " + getMajor(currentContent) + "\n";
        }if (hasAttribute("Minor", currentContent)) {
            description+= "Minor: " + getMinor(currentContent) + "\n";
        }if (hasAttribute("Class", currentContent)) {
            description+= "Class: " + getStudentClass(currentContent) + "\n";
        }if (hasAttribute("Office", currentContent)) {
            description+= "Office: " + getOffice(currentContent) + "\n";
        }if (hasAttribute("Web Page and Description", currentContent)) {
            description+= "Web Page and Description: " + getWebPageDescription(currentContent) + "\n";
        }if (hasAttribute("Affiliation", currentContent)) {
            description+= "Affiliation: " + getAffiliation(currentContent) + "\n";
        }if (hasAttribute("Primary Affiliation", currentContent)) {
            description+= "Primary Affiliation: " + getPrimaryAffiliation(currentContent) + "\n";
        }if (hasAttribute("no results from this search", currentContent)) {
            description+= "There were no generated results for the given entry." + "\n";
        }if (hasAttribute("There were too many results", currentContent)) {
            description+= "Search could not be completed, because there were too many results to display." + "\n";
        }if (hasAttribute("There was no name", currentContent)) {
            description+= "Make sure that you have entered some data before searching." + "\n";
        }if (hasAttribute("No name was found", currentContent)) {
            description+= "No name was found in your query" + "\n";
        }

        holder.resultDescription.setText(description);

        /*if(hasAttribute("Picture",currentContent)){
            String picture = getPicture(currentContent);
            String url = picture.substring(1, picture.length()-1);

            holder.resultPicture = (ImageView) convertView.findViewById(R.id.result_picture);

            imageLoader.displayImage(url, holder.resultPicture, options);
        }*/

        holder.checkBox = (CheckBox) convertView.findViewById(R.id.selectPersonCheckBox);
        if(SearchResultActivity.isInActionMode()){
            holder.checkBox.setVisibility(View.VISIBLE);
        }else{
            holder.checkBox.setVisibility(View.GONE);
            holder.checkBox.setChecked(false);
        }
        holder.cardView = (CardView) convertView.findViewById(R.id.card_view);
        if (!(hasAttribute("no results from this search", currentContent) || hasAttribute("There were too many results", currentContent) || hasAttribute("There was no name", currentContent))) {
            holder.cardView.setOnLongClickListener(this.resultActivity);
        }
        holder.checkBox.setOnClickListener(holder);

        return convertView;
    }

    private class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView resultPicture;
        TextView resultName;
        TextView resultDescription;
        CheckBox checkBox;
        CardView cardView;
        SearchResultActivity resultActivity;

        int position;

        Holder(View view, SearchResultActivity resultActivity){
            super(view);
            this.resultActivity = resultActivity;
        }

        @Override
        public void onClick(View v){

            resultActivity.prepareSelection(v,position);

        }
    }

    // cuts out wanted attribute from a string containing all attributes of a single WPI student
    private String getterHelper(String text, String attribute, int cValue, int counter1) {
        for (int counter = counter1; counter < text.length() - cValue; counter++) {
            if (text.substring(counter, counter + cValue).equals(attribute)) {
                for (int index = counter + cValue; index < text.length() - 8; index++) {
                    if (text.substring(index, index + 10).equals("Department")) {
                        return (text.substring(counter + cValue, index));
                    } else if (text.substring(index, index + 5).equals("Email")) {
                        return (text.substring(counter + cValue, index));
                    } else if (text.substring(index, index + 11).equals("Affiliation")) {
                        return (text.substring(counter + cValue, index));
                    } else if (text.substring(index, index + 19).equals("Primary Affiliation")) {
                        return (text.substring(counter + cValue, index));
                    } else if (text.substring(index, index + 7).equals("Advisor")) {
                        return (text.substring(counter + cValue, index));
                    } else if (text.substring(index, index + 5).equals("Major")) {
                        return (text.substring(counter + cValue, index));
                    } else if (text.substring(index, index + 5).equals("Minor")) {
                        return (text.substring(counter + cValue, index));
                    } else if (text.substring(index, index + 5).equals("Class")) {
                        return (text.substring(counter + cValue, index));
                    } else if (text.substring(index, index + 5).equals("Title")) {
                        return (text.substring(counter + cValue, index));
                    } else if (text.substring(index, index + 6).equals("Office")) {
                        return (text.substring(counter + cValue, index));
                    } else if (text.substring(index, index + 24).equals("Web Page and Description")) {
                        return (text.substring(counter + cValue, index));
                    }else if (text.substring(index, index + 7).equals("Picture")) {
                        return (text.substring(counter + cValue, index));
                    } else if (text.substring(index, index + 4).equals("Name")) {
                        return (text.substring(counter + (cValue),
                                (index - getName(text.substring(index, (text.length() - cValue))).length())));
                    } else if (text.substring(index, index + 6).equals("Search")) {
                        return (text.substring(counter + cValue, index));
                    }
                }

            }
        }
        return ("Attribute doesn't exist.");
    }

    boolean hasAttribute(String attribute, String text)	{
        for(int counter=0; counter <text.length()-attribute.length();counter++){
            if(text.substring(counter, counter+attribute.length()).equals(attribute)){
                return true;
            }
        }
        return false;
    }

    private String getName(String text) {
        return getterHelper(text, "Name", 4, 0);
    }

    private String getDepartment(String text) {
        return getterHelper(text, "Department", 10, 0);
    }


    String getEmail(String text) {
        return getterHelper(text, "Email", 5, 0);
    }


    private String getAffiliation(String text) {
        return getterHelper(text, "Affiliation", 11, 0);
    }

    private String getPrimaryAffiliation(String text) {
        return getterHelper(text, "Primary Affiliation", 19, 0);
    }

    private String getAdvisor(String text) {
        return getterHelper(text, "Advisor", 7, 0);
    }


    private String getMajor(String text) {
        return getterHelper(text, "Major", 5, 0);
    }

    private String getMinor(String text) {
        return getterHelper(text, "Minor", 5, 0);
    }

    private String getStudentClass(String text) {
        return getterHelper(text, "Class", 5, 0);
    }

    private String getTitle(String text) {
        return getterHelper(text, "Title", 5, 0);
    }


    private String getOffice(String text) {
        return getterHelper(text, "Office", 6, 0);
    }


    private String getWebPageDescription(String text) {
        return getterHelper(text, "Web Page and Description", 24, 0);
    }

    private String getPicture(String text) {
        return getterHelper(text, "Picture", 7, 0);
    }
}
