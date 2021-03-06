package android.support.v4.content;

import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;

public class SharedPreferencesCompat
{
  public static class EditorCompat
  {
    private static EditorCompat sInstance;
    private final Helper mHelper;

    private EditorCompat()
    {
      if (Build.VERSION.SDK_INT >= 9)
      {
        this.mHelper = new EditorHelperApi9Impl(null);
        return;
      }
      this.mHelper = new EditorHelperBaseImpl(null);
    }

    public static EditorCompat getInstance()
    {
      if (sInstance == null)
        sInstance = new EditorCompat();
      return sInstance;
    }

    public void apply(@NonNull SharedPreferences.Editor paramEditor)
    {
      this.mHelper.apply(paramEditor);
    }

    private static class EditorHelperApi9Impl
      implements SharedPreferencesCompat.EditorCompat.Helper
    {
      public void apply(@NonNull SharedPreferences.Editor paramEditor)
      {
        EditorCompatGingerbread.apply(paramEditor);
      }
    }

    private static class EditorHelperBaseImpl
      implements SharedPreferencesCompat.EditorCompat.Helper
    {
      public void apply(@NonNull SharedPreferences.Editor paramEditor)
      {
        paramEditor.commit();
      }
    }

    private static abstract interface Helper
    {
      public abstract void apply(@NonNull SharedPreferences.Editor paramEditor);
    }
  }
}

/* Location:           C:\Users\xuetong\Desktop\dazhongdianping7.9.6\ProjectSrc\classes-dex2jar.jar
 * Qualified Name:     android.support.v4.content.SharedPreferencesCompat
 * JD-Core Version:    0.6.0
 */