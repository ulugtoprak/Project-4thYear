package com.project.app.quickquery.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlacesSpeechModel {
    @SerializedName("html_attributions")
    @Expose
    public List<Object> htmlAttributions = null;
    @SerializedName("result")
    @Expose
    public Result result;
    @SerializedName("status")
    @Expose
    public String status;

    public PlacesSpeechModel withHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
        return this;
    }

    public PlacesSpeechModel withResult(Result result) {
        this.result = result;
        return this;
    }

    public PlacesSpeechModel withStatus(String status) {
        this.status = status;
        return this;
    }


    public class AddressComponent {

        @SerializedName("long_name")
        @Expose
        public String longName;
        @SerializedName("short_name")
        @Expose
        public String shortName;
        @SerializedName("types")
        @Expose
        public List<String> types = null;

        public AddressComponent withLongName(String longName) {
            this.longName = longName;
            return this;
        }

        public AddressComponent withShortName(String shortName) {
            this.shortName = shortName;
            return this;
        }

        public AddressComponent withTypes(List<String> types) {
            this.types = types;
            return this;
        }

    }
    public class Geometry {

        @SerializedName("location")
        @Expose
        public Location location;
        @SerializedName("viewport")
        @Expose
        public Viewport viewport;

        public Geometry withLocation(Location location) {
            this.location = location;
            return this;
        }

        public Geometry withViewport(Viewport viewport) {
            this.viewport = viewport;
            return this;
        }

    }

    public class Location {

        @SerializedName("lat")
        @Expose
        public float lat;
        @SerializedName("lng")
        @Expose
        public float lng;

        public Location withLat(float lat) {
            this.lat = lat;
            return this;
        }

        public Location withLng(float lng) {
            this.lng = lng;
            return this;
        }

    }

    public class Northeast {

        @SerializedName("lat")
        @Expose
        public float lat;
        @SerializedName("lng")
        @Expose
        public float lng;

        public Northeast withLat(float lat) {
            this.lat = lat;
            return this;
        }

        public Northeast withLng(float lng) {
            this.lng = lng;
            return this;
        }

    }

    public class Photo {

        @SerializedName("height")
        @Expose
        public int height;
        @SerializedName("html_attributions")
        @Expose
        public List<String> htmlAttributions = null;
        @SerializedName("photo_reference")
        @Expose
        public String photoReference;
        @SerializedName("width")
        @Expose
        public int width;

        public Photo withHeight(int height) {
            this.height = height;
            return this;
        }

        public Photo withHtmlAttributions(List<String> htmlAttributions) {
            this.htmlAttributions = htmlAttributions;
            return this;
        }

        public Photo withPhotoReference(String photoReference) {
            this.photoReference = photoReference;
            return this;
        }

        public Photo withWidth(int width) {
            this.width = width;
            return this;
        }

    }



    public class Result {

        @SerializedName("address_components")
        @Expose
        public List<AddressComponent> addressComponents = null;
        @SerializedName("adr_address")
        @Expose
        public String adrAddress;
        @SerializedName("formatted_address")
        @Expose
        public String formattedAddress;
        @SerializedName("geometry")
        @Expose
        public Geometry geometry;
        @SerializedName("icon")
        @Expose
        public String icon;
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("photos")
        @Expose
        public List<Photo> photos = null;
        @SerializedName("place_id")
        @Expose
        public String placeId;
        @SerializedName("reference")
        @Expose
        public String reference;
        @SerializedName("scope")
        @Expose
        public String scope;
        @SerializedName("types")
        @Expose
        public List<String> types = null;
        @SerializedName("url")
        @Expose
        public String url;
        @SerializedName("utc_offset")
        @Expose
        public int utcOffset;

        public Result withAddressComponents(List<AddressComponent> addressComponents) {
            this.addressComponents = addressComponents;
            return this;
        }

        public Result withAdrAddress(String adrAddress) {
            this.adrAddress = adrAddress;
            return this;
        }

        public Result withFormattedAddress(String formattedAddress) {
            this.formattedAddress = formattedAddress;
            return this;
        }

        public Result withGeometry(Geometry geometry) {
            this.geometry = geometry;
            return this;
        }

        public Result withIcon(String icon) {
            this.icon = icon;
            return this;
        }

        public Result withId(String id) {
            this.id = id;
            return this;
        }

        public Result withName(String name) {
            this.name = name;
            return this;
        }

        public Result withPhotos(List<Photo> photos) {
            this.photos = photos;
            return this;
        }

        public Result withPlaceId(String placeId) {
            this.placeId = placeId;
            return this;
        }

        public Result withReference(String reference) {
            this.reference = reference;
            return this;
        }

        public Result withScope(String scope) {
            this.scope = scope;
            return this;
        }

        public Result withTypes(List<String> types) {
            this.types = types;
            return this;
        }

        public Result withUrl(String url) {
            this.url = url;
            return this;
        }

        public Result withUtcOffset(int utcOffset) {
            this.utcOffset = utcOffset;
            return this;
        }

    }

    public class Southwest {

        @SerializedName("lat")
        @Expose
        public float lat;
        @SerializedName("lng")
        @Expose
        public float lng;

        public Southwest withLat(float lat) {
            this.lat = lat;
            return this;
        }

        public Southwest withLng(float lng) {
            this.lng = lng;
            return this;
        }

    }

    public class Viewport {

        @SerializedName("northeast")
        @Expose
        public Northeast northeast;
        @SerializedName("southwest")
        @Expose
        public Southwest southwest;

        public Viewport withNortheast(Northeast northeast) {
            this.northeast = northeast;
            return this;
        }

        public Viewport withSouthwest(Southwest southwest) {
            this.southwest = southwest;
            return this;
        }

    }

}
