package com.teamll.expectlauncher.ui.main.bottomsheet;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import android.widget.ImageView;
import android.widget.TextView;


import com.teamll.expectlauncher.ExpectLauncher;
import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.ui.main.LayoutSwitcher;
import com.teamll.expectlauncher.ui.main.appdrawer.AppDrawerFragment;
import com.teamll.expectlauncher.ui.widgets.numberpicker.NumberPicker;
import com.teamll.expectlauncher.ui.widgets.rangeseekbar.RangeSeekBar;
import com.teamll.expectlauncher.util.Tool;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;

public class CommonSettingBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener , NumberPicker.OnValueChangeListener {
    private static final String TAG ="CommonSettingBottomSheet";

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
       if(rangeChangedListener!=null) rangeChangedListener.onValueChange(picker,oldVal,newVal);
    }


    public interface BottomSheetListener {
        void onClickButtonInsideBottomSheet(View view);
    }
    BottomSheetListener listener;
    AppDrawerFragment rangeChangedListener;

    LayoutSwitcher.MODE mode;
    RangeSeekBar rangeSeekBar;
    NumberPicker mNumberPicker;
    public void setListener(BottomSheetListener listener) {
        this.listener = listener;
    }
    public void setAppDrawer(AppDrawerFragment fragment) {
        this.listener = fragment;
        this.rangeChangedListener = fragment;

    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    public static CommonSettingBottomSheet newInstance(LayoutSwitcher.MODE mode) {
        CommonSettingBottomSheet fragment = new CommonSettingBottomSheet();
        fragment.mode = mode;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bsd = new BottomSheetDialog(requireContext(), getTheme());

        return bsd;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view;
        int[] ids;
        if (mode == LayoutSwitcher.MODE.IN_MAIN_SCREEN) {
            view = inflater.inflate(R.layout.mainscreen_bottom_sheet_layout, container,
                    false);
            ids = new int[] {
                    R.id.toggleButton, R.id.add_widget, R.id.wallpaper, R.id.wall_editor
            };
        } else {
            view = inflater.inflate(R.layout.app_drawer_bottom_sheet_layout, container, false);
            ids = new int[] {
                    R.id.toggleButton, R.id.show_title, R.id.position, R.id.app_icon_editor,R.id.launcher_setting,R.id.app_config
            };

            rangeSeekBar = view.findViewById(R.id.seek_bar);
            rangeSeekBar.setValue(ExpectLauncher.getInstance().getPreferencesUtility().getAppIconSize() * 100);

            mNumberPicker = view.findViewById(R.id.number_picker);
            mNumberPicker.setValue(ExpectLauncher.getInstance().getPreferencesUtility().getFontTitleSize(getContext()));
            mNumberPicker.setOnValueChangedListener(this);

            if (!ExpectLauncher.getInstance().getPreferencesUtility().isShowAppTitle()) {
                // detect hidden app title
                ((FloatingActionButton) view.findViewById(R.id.show_title)).setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                ((TextView) view.findViewById(R.id.show_title_text)).setText(R.string.hidden_app_title);
            }

            if (rangeChangedListener != null)
                rangeSeekBar.setOnRangeChangedListener(rangeChangedListener);
            int color = Tool.getSurfaceColor();
            mNumberPicker.setSelectedTextColor(color);
            rangeSeekBar.setProgressColor(color);

            mNumberPicker.setTextColor(Color.argb(0x88,Color.red(color),Color.green(color),Color.blue(color)));
        }

        // set on click for all buttons
        for (int i:
                ids) {
            View img = view.findViewById(i);
            img.setOnClickListener(this);
            if(img instanceof ImageView) {
                ((ImageView)img).setColorFilter(Tool.getSurfaceColor());
            } else if(img instanceof TextView) {
                ((TextView)img).setTextColor(Tool.getSurfaceColor());
            }
        }

        return view;
    }


    @Override
    public void onClick(final View view) {
        if (view.getId() == R.id.toggleButton) {
            this.dismiss();
            return;
        }
        if (listener != null) {
            if(view.getId()==R.id.show_title) {
                listener.onClickButtonInsideBottomSheet(view);
                return;
            }
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    listener.onClickButtonInsideBottomSheet(view);
                }
            }, 100);
        }
        this.dismiss();
    }
    /**
     private ArrayList<View> rippleViews = new ArrayList<>();
     private boolean first_time = true;
     public void addToBeRipple(int drawable,View... v) {
     if(first_time) {
     first_time = false;
     res = getResources();
     }
     int l = v.length;
     rippleViews.addAll(Arrays.asList(v));
     for(View view :v) {
     view.setBackground( (RippleDrawable) res.getDrawable(drawable));
     view.setClickable(true);
     }
     }
     Resources res;
     public void applyRippleColor(int color) {
     for( final View v : rippleViews)
     {
     ((RippleDrawable)v.getBackground()).setColor(ColorStateList.valueOf(color));
     }
     }

     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
                FrameLayout bottomSheet = (FrameLayout)
                        dialog.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(-Tool.getNavigationHeight(requireActivity()));
                behavior.setHideable(false);
                behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == STATE_COLLAPSED)
                            CommonSettingBottomSheet.this.dismiss();
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });
            }
        });
    }
}