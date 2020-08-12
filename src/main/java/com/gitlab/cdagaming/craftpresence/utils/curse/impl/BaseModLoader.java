package com.gitlab.cdagaming.craftpresence.utils.curse.impl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseModLoader {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("gameVersionId")
    @Expose
    public Integer gameVersionId;
    @SerializedName("minecraftGameVersionId")
    @Expose
    public Integer minecraftGameVersionId;
    @SerializedName("forgeVersion")
    @Expose
    public String forgeVersion;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("type")
    @Expose
    public Integer type;
    @SerializedName("downloadUrl")
    @Expose
    public String downloadUrl;
    @SerializedName("filename")
    @Expose
    public String filename;
    @SerializedName("installMethod")
    @Expose
    public Integer installMethod;
    @SerializedName("latest")
    @Expose
    public Boolean latest;
    @SerializedName("recommended")
    @Expose
    public Boolean recommended;
    @SerializedName("approved")
    @Expose
    public Boolean approved;
    @SerializedName("dateModified")
    @Expose
    public String dateModified;
    @SerializedName("mavenVersionString")
    @Expose
    public String mavenVersionString;
    @SerializedName("versionJson")
    @Expose
    public String versionJson;
    @SerializedName("librariesInstallLocation")
    @Expose
    public String librariesInstallLocation;
    @SerializedName("minecraftVersion")
    @Expose
    public String minecraftVersion;
    @SerializedName("modLoaderGameVersionId")
    @Expose
    public Integer modLoaderGameVersionId;
    @SerializedName("modLoaderGameVersionTypeId")
    @Expose
    public Integer modLoaderGameVersionTypeId;
    @SerializedName("modLoaderGameVersionStatus")
    @Expose
    public Integer modLoaderGameVersionStatus;
    @SerializedName("modLoaderGameVersionTypeStatus")
    @Expose
    public Integer modLoaderGameVersionTypeStatus;
    @SerializedName("mcGameVersionId")
    @Expose
    public Integer mcGameVersionId;
    @SerializedName("mcGameVersionTypeId")
    @Expose
    public Integer mcGameVersionTypeId;
    @SerializedName("mcGameVersionStatus")
    @Expose
    public Integer mcGameVersionStatus;
    @SerializedName("mcGameVersionTypeStatus")
    @Expose
    public Integer mcGameVersionTypeStatus;
    @SerializedName("installProfileJson")
    @Expose
    public String installProfileJson;

}