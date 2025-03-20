package videos.religious.platform;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class ExtendedViewPager extends ViewPager {
    public ExtendedViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
    /* access modifiers changed from: protected */
    @Override
    public boolean canScroll(View view, boolean z, int i, int i2, int i3) {
        if (view instanceof ExtendedWebview) {
            return ((ExtendedWebview) view).canScrollHor(-i);
        }
        return super.canScroll(view, z, i, i2, i3);
    }
}