package com.denofdevelopers.mktolls.model;

import android.os.Parcel;
import android.os.Parcelable;

public final class Toll implements Parcelable {

    public final double lat;
    public final double lng;
    public final String tollName;
    public final String tollNameMk;

    public final double categoryOneADen;
    public final double categoryOneDen;
    public final double categoryTwoDen;
    public final double categoryThreeDen;
    public final double categoryFourDen;

    public final double categoryOneEuro;
    public final double categoryOneAEuro;
    public final double categoryTwoEuro;
    public final double categoryThreeEuro;
    public final double categoryFourEuro;

    public Toll(double lat, double lng, String tollName, String tollNameMk, double categoryOneADen, double categoryOneDen, double categoryTwoDen, double categoryThreeDen,
                double categoryFourDen, double categoryOneAEuro, double categoryOneEuro, double categoryTwoEuro, double categoryThreeEuro,
                double categoryFourEuro) {
        this.lat = lat;
        this.lng = lng;
        this.tollName = tollName;
        this.tollNameMk = tollNameMk;
        this.categoryOneADen = categoryOneADen;
        this.categoryOneDen = categoryOneDen;
        this.categoryTwoDen = categoryTwoDen;
        this.categoryThreeDen = categoryThreeDen;
        this.categoryFourDen = categoryFourDen;
        this.categoryOneAEuro = categoryOneAEuro;
        this.categoryOneEuro = categoryOneEuro;
        this.categoryTwoEuro = categoryTwoEuro;
        this.categoryThreeEuro = categoryThreeEuro;
        this.categoryFourEuro = categoryFourEuro;
    }

    protected Toll(Parcel in) {
        lat = in.readDouble();
        lng = in.readDouble();
        tollName = in.readString();
        tollNameMk = in.readString();
        categoryOneADen = in.readDouble();
        categoryOneDen = in.readDouble();
        categoryTwoDen = in.readDouble();
        categoryThreeDen = in.readDouble();
        categoryFourDen = in.readDouble();
        categoryOneAEuro = in.readDouble();
        categoryOneEuro = in.readDouble();
        categoryTwoEuro = in.readDouble();
        categoryThreeEuro = in.readDouble();
        categoryFourEuro = in.readDouble();
    }

    public static final Creator<Toll> CREATOR = new Creator<Toll>() {
        @Override
        public Toll createFromParcel(Parcel in) {
            return new Toll(in);
        }

        @Override
        public Toll[] newArray(int size) {
            return new Toll[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(tollName);
        dest.writeString(tollNameMk);
        dest.writeDouble(categoryOneADen);
        dest.writeDouble(categoryOneDen);
        dest.writeDouble(categoryTwoDen);
        dest.writeDouble(categoryThreeDen);
        dest.writeDouble(categoryFourDen);
        dest.writeDouble(categoryOneAEuro);
        dest.writeDouble(categoryOneEuro);
        dest.writeDouble(categoryTwoEuro);
        dest.writeDouble(categoryThreeEuro);
        dest.writeDouble(categoryFourEuro);
    }
}
