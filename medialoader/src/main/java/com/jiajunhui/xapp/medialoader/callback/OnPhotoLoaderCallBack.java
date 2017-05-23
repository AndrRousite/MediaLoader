package com.jiajunhui.xapp.medialoader.callback;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.Loader;

import com.jiajunhui.xapp.medialoader.bean.PhotoFolder;
import com.jiajunhui.xapp.medialoader.bean.PhotoItem;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DISPLAY_NAME;

/**
 * Created by Taurus on 2017/5/23.
 */

public abstract class OnPhotoLoaderCallBack extends OnMediaLoaderCallBack<PhotoFolder,PhotoItem> {

    @Override
    public void onLoadFinish(Loader<Cursor> loader, Cursor data) {
        List<PhotoFolder> folders = new ArrayList<>();
        if(data == null){
            onResult(folders,null);
            return;
        }
        List<PhotoItem> allPhotos = new ArrayList<>();
        PhotoFolder folder;
        PhotoItem item;
        while (data.moveToNext()) {
            String folderId = data.getString(data.getColumnIndexOrThrow(BUCKET_ID));
            String folderName = data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
            int imageId = data.getInt(data.getColumnIndexOrThrow(_ID));
            String name = data.getString(data.getColumnIndexOrThrow(DISPLAY_NAME));
            String path = data.getString(data.getColumnIndexOrThrow(DATA));
            folder = new PhotoFolder();
            folder.setId(folderId);
            folder.setName(folderName);
            item = new PhotoItem(imageId,name,path);
            if(folders.contains(folder)){
                folders.get(folders.indexOf(folder)).addItem(item);
            }else{
                folder.setCover(path);
                folder.addItem(item);
                folders.add(folder);
            }
            allPhotos.add(item);
        }
        onResult(folders,allPhotos);
    }

    @Override
    public String[] getSelectProjection() {
        String[] PROJECTION = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_MODIFIED
        };
        return PROJECTION;
    }

    @Override
    public Uri getQueryUri() {
        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }
}
