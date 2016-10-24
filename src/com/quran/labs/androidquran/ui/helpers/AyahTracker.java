package com.quran.labs.androidquran.ui.helpers;

import com.quran.labs.androidquran.common.Response;

import android.graphics.drawable.BitmapDrawable;

import java.util.Set;

public interface AyahTracker {
  public void highlightAyah(int sura, int ayah, HighlightType type);
  public void highlightAyah(int sura, int ayah, HighlightType type, boolean scrollToAyah);
  public void highlightAyat(
      int page, Set<String> ayahKeys, HighlightType type);
  public void unHighlightAyah(int sura, int ayah, HighlightType type);
  public void unHighlightAyahs(HighlightType type);
  public void updateView();
  public void onLoadImageResponse(BitmapDrawable drawable, Response response);
}