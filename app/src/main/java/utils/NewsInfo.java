package utils;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bunny on 21/04/17.
 */

public class NewsInfo implements Parcelable {

    private String newsHeadline;
    private String newsSummary;
    private String newsSource;
    private String newsCategory;
    private String newsDate;
    private String newsTime;
    private String newsNotify;
    private String newsImageLink;
    private Bitmap newsImage;
    private HashMap<String, NewsSourceList> newsSourceListHashMap;


    ArrayList<NewsSourceList> newsSourceListArrayList;
    int newsSourceimageIndex = 0;

    private HashMap<String, Long> newsTweetListHashMap;
    String newsSourceShort = "";

    public NewsInfo() {
    }

    public String getNewsSourceShort() {
        return newsSourceShort;
    }

    public void setNewsSourceShort(String newsSourceShort) {
        this.newsSourceShort = newsSourceShort;
    }

    public String getNewsTime() {
        return newsTime;
    }

    public void setNewsTime(String newsTime) {
        this.newsTime = newsTime;
    }

    public int getNewsSourceimageIndex() {
        return newsSourceimageIndex;
    }

    public void setNewsSourceimageIndex(int newsSourceimageIndex) {
        this.newsSourceimageIndex = newsSourceimageIndex;
    }

    public HashMap<String, Long> getNewsTweetListHashMap() {
        return newsTweetListHashMap;
    }

    public void setNewsTweetListHashMap(HashMap<String, Long> newsTweetListHashMap) {
        this.newsTweetListHashMap = newsTweetListHashMap;
    }

    @Override
    public String toString() {
        return "NewsInfo{" +
                "newsHeadline='" + newsHeadline + '\'' +
                ", newsSummary='" + newsSummary + '\'' +
                ", newsSource='" + newsSource + '\'' +
                ", newsCategory='" + newsCategory + '\'' +
                ", newsDate='" + newsDate + '\'' +
                ", newsTime='" + newsTime + '\'' +
                ", newsNotify='" + newsNotify + '\'' +
                ", newsImageLink='" + newsImageLink + '\'' +
                ", newsImage=" + newsImage +
                ", newsSourceListHashMap=" + newsSourceListHashMap +
                ", newsSourceListArrayList=" + newsSourceListArrayList +
                '}';
    }


    public ArrayList<NewsSourceList> getNewsSourceListArrayList() {
        return newsSourceListArrayList;
    }

    public void setNewsSourceListArrayList(ArrayList<NewsSourceList> newsSourceListArrayList) {
        this.newsSourceListArrayList = newsSourceListArrayList;
    }


    public HashMap<String, NewsSourceList> getNewsSourceListHashMap() {
        return newsSourceListHashMap;
    }

    public void setNewsSourceListHashMap(HashMap<String, NewsSourceList> newsSourceListHashMap) {
        this.newsSourceListHashMap = newsSourceListHashMap;
    }

    public String getNewsHeadline() {
        return newsHeadline;
    }

    public void setNewsHeadline(String newsHeadline) {
        this.newsHeadline = newsHeadline;
    }

    public String getNewsSummary() {
        return newsSummary;
    }

    public void setNewsSummary(String newsSummary) {
        this.newsSummary = newsSummary;
    }

    public String getNewsSource() {
        return newsSource;
    }

    public void setNewsSource(String newsSource) {
        this.newsSource = newsSource;
    }

    public String getNewsCategory() {
        return newsCategory;
    }

    public void setNewsCategory(String newsCategory) {
        this.newsCategory = newsCategory;
    }

    public String getNewsDate() {
        return newsDate;
    }

    public void setNewsDate(String newsDate) {
        this.newsDate = newsDate;
    }


    public String getNewsNotify() {
        return newsNotify;
    }

    public void setNewsNotify(String newsNotify) {
        this.newsNotify = newsNotify;
    }

    public String getNewsImageLink() {
        return newsImageLink;
    }

    public void setNewsImageLink(String newsImageLink) {
        this.newsImageLink = newsImageLink;
    }

    public Bitmap getNewsImage() {
        return newsImage;
    }

    public void setNewsImage(Bitmap newsImage) {
        this.newsImage = newsImage;
    }

    public void resolveHashmap() {
        newsSourceListArrayList = new ArrayList<>();
        if (this.newsSourceListHashMap != null) {
            for (NewsSourceList newsSourceList : newsSourceListHashMap.values()) {
                newsSourceListArrayList.add(newsSourceList);

            }
        }
        newsSourceListHashMap = null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(newsHeadline);
        dest.writeString(newsSummary);
        dest.writeString(newsSource);
        dest.writeString(newsCategory);
        dest.writeString(newsDate);
        //dest.writeLong(newsTime);
        dest.writeParcelable(newsImage, 1);
        dest.writeString(newsNotify);
        dest.writeString(newsImageLink);


    }

    private NewsInfo(Parcel in) {

        this.newsHeadline = in.readString();
        this.newsSummary = in.readString();
        this.newsSource = in.readString();
        this.newsCategory = in.readString();
        this.newsDate = in.readString();
        //this.newsTime = in.readLong();
        this.newsNotify = in.readString();
        this.newsImageLink = in.readString();


    }

    public static final Parcelable.Creator<NewsInfo> CREATOR = new Parcelable.Creator<NewsInfo>() {

        @Override
        public NewsInfo createFromParcel(Parcel source) {
            return new NewsInfo(source);
        }

        @Override
        public NewsInfo[] newArray(int size) {
            return new NewsInfo[size];
        }
    };

/*

    public static String resolveDateString(long newsTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(newsTime);

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);


        Calendar currentTime = Calendar.getInstance();
        int mCurrentYear = currentTime.get(Calendar.YEAR);
        int mCurrentMonth = currentTime.get(Calendar.MONTH);
        int mCurrentDay = currentTime.get(Calendar.DAY_OF_MONTH);

        if (mYear == mCurrentYear) {
            if (mCurrentMonth == mMonth) {


                if (mCurrentDay - mDay == 0) {
                    return "Today";
                } else if (mCurrentDay - mDay == 1) {
                    return "Yesterday";
                } else if (mCurrentDay - mDay < 7) {
                    return mCurrentDay - mDay + " days ago";
                } else if (mCurrentDay - mDay < 14) {
                    return "1 week ago";
                } else if (mCurrentDay - mDay < 21) {
                    return "2 week ago";
                } else if (mCurrentDay - mDay < 28) {
                    return "3 week ago";
                } else if (mCurrentDay - mDay < 35) {
                    return "4 week ago";
                }


            } else {
                if (mCurrentMonth - mMonth < 12) {
                    return mCurrentMonth - mMonth + " month ago";
                }

            }

        } else {
            return mCurrentYear - mYear + " year ago";

        }

        return "";

    }
*/

    public static String resolveDateString(long newsTime) {
        Calendar calendar = Calendar.getInstance();


        long currenttime = calendar.getTimeInMillis();


        //calculate difference in time
        //long timeDifference = (currenttime - newsTime);

        if ((currenttime - newsTime) <= 0 || newsTime <= 1493013649175l) {
            return "";
        }

        long numberOfHour = (currenttime - newsTime) / 3600000;
        if (numberOfHour == 0) {
            return "less than hour ago";
        } else if (numberOfHour < 24) {
            return String.valueOf(numberOfHour) + " hour ago";
        } else {

            long numberOfDays = numberOfHour / 24;

            if (numberOfDays < 7) {
                return String.valueOf(numberOfDays) + " day ago";
            } else {

                long numberOfWeek = numberOfDays / 7;
                if (numberOfWeek <= 4) {
                    return String.valueOf(numberOfWeek) + " week ago";
                } else {

                    long numberOfMonth = numberOfWeek / 4;
                    if (numberOfMonth <= 12) {
                        return String.valueOf(numberOfMonth) + " month ago";
                    } else {

                        long numberOfYear = numberOfMonth / 12;

                        return String.valueOf(numberOfYear) + " year ago";

                    }

                }

            }

        }


    }


}





