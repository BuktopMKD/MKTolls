package com.denofdevelopers.mktolls.screen.screen.result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.denofdevelopers.mktolls.R;
import com.denofdevelopers.mktolls.model.Toll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    private List<Toll> tollList;
    private Context context;
    private String selectedCategory;

    public ResultAdapter(Context context, String selectedCategory) {
        this.context = context;
        tollList = new ArrayList<>();
        this.selectedCategory = selectedCategory;
    }

    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result_toll, null);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {
        Toll toll = tollList.get(position);
        holder.bind(toll, position);
    }

    @Override
    public int getItemCount() {
        return (tollList != null ? tollList.size() : 0);
    }

    public void initializeList(List<Toll> tollList) {
        this.tollList.clear();
        Collections.reverse(tollList);
        this.tollList.addAll(tollList);
        notifyDataSetChanged();
    }

    class ResultViewHolder extends RecyclerView.ViewHolder {

        Toll toll;
        int position;

        @BindView(R.id.toll_name)
        TextView tollName;
        @BindView(R.id.toll_charge_denars)
        TextView tollChargeDenars;
        @BindView(R.id.toll_charge_euros)
        TextView tollChargeEuros;

        ResultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Toll toll, int position) {
            this.toll = toll;
            this.position = position;
            if (Locale.getDefault().getLanguage().equals("mk")) {
                tollName.setText(toll.tollNameMk);
            } else {
                tollName.setText(toll.tollName);
            }
            if (selectedCategory.equals(context.getString(R.string.category_1A))) {
                tollChargeDenars.setText(context.getString(R.string.format_denars, toll.categoryOneADen));
                tollChargeEuros.setText(context.getString(R.string.format_euro, toll.categoryOneAEuro));
            } else if (selectedCategory.equals(context.getString(R.string.category_1B))) {
                tollChargeDenars.setText(context.getString(R.string.format_denars, toll.categoryOneDen));
                tollChargeEuros.setText(context.getString(R.string.format_euro, toll.categoryOneEuro));
            } else if (selectedCategory.equals(context.getString(R.string.category_2))) {
                tollChargeDenars.setText(context.getString(R.string.format_denars, toll.categoryTwoDen));
                tollChargeEuros.setText(context.getString(R.string.format_euro, toll.categoryTwoEuro));
            } else if (selectedCategory.equals(context.getString(R.string.category_3))) {
                tollChargeDenars.setText(context.getString(R.string.format_denars, toll.categoryThreeDen));
                tollChargeEuros.setText(context.getString(R.string.format_euro, toll.categoryThreeEuro));
            } else {
                tollChargeDenars.setText(context.getString(R.string.format_denars, toll.categoryFourDen));
                tollChargeEuros.setText(context.getString(R.string.format_euro, toll.categoryFourEuro));
            }
        }
    }
}
