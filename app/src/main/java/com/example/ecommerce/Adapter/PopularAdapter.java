package com.example.ecommerce.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.ecommerce.Activity.DetailsActivity;
import com.example.ecommerce.databinding.ViewholderPopListBinding;
import com.example.ecommerce.domain.ItemsDomain;

import java.util.ArrayList;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.Viewholder> {
    ArrayList<ItemsDomain> items;
    Context context;

    public PopularAdapter(ArrayList<ItemsDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public PopularAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderPopListBinding binding = ViewholderPopListBinding.inflate(LayoutInflater.from(context), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularAdapter.Viewholder holder, @SuppressLint("RecyclerView") int position) {
        if (items != null && position < items.size()) {
            ItemsDomain item = items.get(position);
            if (item != null) {
                holder.binding.title.setText(item.getProductName());
                holder.binding.reviewTxt.setText("" + item.getReview());
                holder.binding.priceTxt.setText("₹" + item.getproductPrice());
                holder.binding.ratingTxt.setText("(" + item.getRating() + ")");
                holder.binding.oldPriceTxt.setText("₹" + item.getproductMrpPrice());
                holder.binding.oldPriceTxt.setPaintFlags(holder.binding.oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.binding.ratingBar.setRating((float) item.getRating());

                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transform(new CenterCrop());

                // Load the single image URL directly
                if (item.getproductImage() != null && !item.getproductImage().isEmpty()) {
                    Glide.with(context)
                            .load(item.getproductImage()) // Use the single image URL
                            .apply(requestOptions)
                            .into(holder.binding.pic);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, DetailsActivity.class);
                        intent.putExtra("object", item);
                        context.startActivity(intent);
                    }
                });
            }
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderPopListBinding binding;

        public Viewholder(ViewholderPopListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
