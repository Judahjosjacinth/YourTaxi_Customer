// Generated code from Butter Knife. Do not modify!
package com.ytspilot;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class BidActivity_ViewBinding implements Unbinder {
  private BidActivity target;

  private View view7f080062;

  @UiThread
  public BidActivity_ViewBinding(BidActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public BidActivity_ViewBinding(final BidActivity target, View source) {
    this.target = target;

    View view;
    target.tvBookingIDNumber = Utils.findRequiredViewAsType(source, R.id.tv_bookingid_number, "field 'tvBookingIDNumber'", TextView.class);
    target.tvTripType = Utils.findRequiredViewAsType(source, R.id.tv_triptype_details, "field 'tvTripType'", TextView.class);
    target.layoutLocal = Utils.findRequiredViewAsType(source, R.id.layout_local, "field 'layoutLocal'", LinearLayout.class);
    target.layoutOutstation = Utils.findRequiredViewAsType(source, R.id.layout_outstation, "field 'layoutOutstation'", LinearLayout.class);
    target.etMinAmount = Utils.findRequiredViewAsType(source, R.id.et_min_amount, "field 'etMinAmount'", EditText.class);
    target.etAdditonalKM = Utils.findRequiredViewAsType(source, R.id.et_additional_km, "field 'etAdditonalKM'", EditText.class);
    target.etAdditionalHour = Utils.findRequiredViewAsType(source, R.id.et_additional_hour, "field 'etAdditionalHour'", EditText.class);
    target.etDriverBatta = Utils.findRequiredViewAsType(source, R.id.et_driverbatta, "field 'etDriverBatta'", EditText.class);
    target.etEstimatedPermit = Utils.findRequiredViewAsType(source, R.id.et_estimated_permit, "field 'etEstimatedPermit'", EditText.class);
    target.etPricePerKm = Utils.findRequiredViewAsType(source, R.id.et_price_per_km, "field 'etPricePerKm'", EditText.class);
    view = Utils.findRequiredView(source, R.id.btn_bid, "field 'btnBid' and method 'submit'");
    target.btnBid = Utils.castView(view, R.id.btn_bid, "field 'btnBid'", Button.class);
    view7f080062 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.submit();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    BidActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.tvBookingIDNumber = null;
    target.tvTripType = null;
    target.layoutLocal = null;
    target.layoutOutstation = null;
    target.etMinAmount = null;
    target.etAdditonalKM = null;
    target.etAdditionalHour = null;
    target.etDriverBatta = null;
    target.etEstimatedPermit = null;
    target.etPricePerKm = null;
    target.btnBid = null;

    view7f080062.setOnClickListener(null);
    view7f080062 = null;
  }
}
