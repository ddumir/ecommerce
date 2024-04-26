package com.example.ecommerce.Activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.example.ecommerce.Adapter.CategoryAdapter;
import com.example.ecommerce.Adapter.PopularAdapter;
import com.example.ecommerce.Adapter.SliderAdapter;
import com.example.ecommerce.MainActivity2;
import com.example.ecommerce.databinding.ActivityHomeBinding;
import com.example.ecommerce.domain.CategoryDomain;
import com.example.ecommerce.domain.ItemsDomain;
import com.example.ecommerce.domain.SliderItems;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class home extends BaseActivity {
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initBanner();
//        initCategory();
        initPopular();
        bottomNavigation();
    }

    private void bottomNavigation() {
        binding.cartBtn.setOnClickListener(v -> startActivity(new Intent(home.this, CartActivity.class)));
    }

    private void initPopular() {
        CollectionReference itemsCollection = firestore.collection("products");
        binding.progressBarPopular.setVisibility(View.VISIBLE);
        ArrayList<ItemsDomain> items = new ArrayList<>();

        itemsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            ItemsDomain item = documentSnapshot.toObject(ItemsDomain.class);
                            if (item != null) {
                                items.add(item);
                            }
                        }

                        if (!items.isEmpty()) {
                            binding.recyclerviewPopular.setLayoutManager(new GridLayoutManager(home.this, 2));
                            binding.recyclerviewPopular.setAdapter(new PopularAdapter(items));
                        }
                    }
                }
                binding.progressBarPopular.setVisibility(View.GONE);
            }
        });
    }

    private void initCategory() {
        DatabaseReference myref = database.getReference("Category");
        binding.progressBarOffical.setVisibility(View.VISIBLE);
        ArrayList<CategoryDomain> items=new ArrayList<>();
        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        items.add(issue.getValue(CategoryDomain.class));
                    }
                    if(!items.isEmpty()){
                        binding.recyclerViewOfficial.setLayoutManager(new LinearLayoutManager(home.this,
                                LinearLayoutManager.HORIZONTAL,false));
                        binding.recyclerViewOfficial.setAdapter(new CategoryAdapter(items));

                    }
                    binding.progressBarOffical.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private void initBanner() {
//        DatabaseReference myRef = database.getReference("Banner");
//        binding.progressBarBanner.setVisibility(View.VISIBLE);
//        ArrayList<SliderItems> items = new ArrayList<>();
//        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    for (DataSnapshot issue : snapshot.getChildren()) {
//                        items.add(issue.getValue(SliderItems.class));
//                    }
//                    banners(items);
//                    binding.progressBarBanner.setVisibility(View.GONE);
//                }
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    private void banners(ArrayList<SliderItems> items) {
//
//        binding.viewpagerSlider.setAdapter(new SliderAdapter(items, binding.viewpagerSlider));
//        binding.viewpagerSlider.setClipToPadding(false);
//        binding.viewpagerSlider.setClipChildren(false);
//        binding.viewpagerSlider.setOffscreenPageLimit(3);
//        binding.viewpagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
//
//        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
//        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
//
//        binding.viewpagerSlider.setPageTransformer(compositePageTransformer);
//    }
private void initBanner() {
    CollectionReference bannersRef = firestore.collection("app-dynamic");
    binding.progressBarBanner.setVisibility(View.VISIBLE);
    ArrayList<SliderItems> items = new ArrayList<>();

    bannersRef.whereEqualTo("type", "banner")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.contains("bannerFile")) {
                                Map<String, Object> bannerFileData = (Map<String, Object>) document.getData().get("bannerFile");
                                if (bannerFileData != null && bannerFileData.containsKey("url")) {
                                    String imageUrl = bannerFileData.get("url").toString();
                                    items.add(new SliderItems(imageUrl)); // Assuming SliderItems constructor accepts image URL
                                }
                            }
                        }
                        banners(items);
                        binding.progressBarBanner.setVisibility(View.GONE);
                    } else {
                        Log.e(TAG, "Error getting banners: ", task.getException());
                        binding.progressBarBanner.setVisibility(View.GONE);
                    }
                }
            });
}

    private void banners(ArrayList<SliderItems> items) {
        if (items.isEmpty()) {
            // Handle case when there are no items to display
            return;
        }
        SliderAdapter sliderAdapter = new SliderAdapter(items, binding.viewpagerSlider);
        binding.viewpagerSlider.setAdapter(sliderAdapter);

        // Customize ViewPager settings
        binding.viewpagerSlider.setClipToPadding(false);
        binding.viewpagerSlider.setClipChildren(false);
        binding.viewpagerSlider.setOffscreenPageLimit(3);
        binding.viewpagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        // Apply page transformer
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        binding.viewpagerSlider.setPageTransformer(compositePageTransformer);
    }

    public void pro_file(View vi){
        Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }

}