//package com.example.ecommerceapplication.adapters;
//
//import android.graphics.drawable.ColorDrawable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.AsyncListDiffer;
//import androidx.recyclerview.widget.DiffUtil;
//import androidx.recyclerview.widget.RecyclerView;
//
//public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.ColorsViewHolder> {
//
//    private int selectedPosition = -1;
//
//    public static class ColorsViewHolder extends RecyclerView.ViewHolder {
//        private final ColorRvItemBinding binding;
//
//        public ColorsViewHolder(ColorRvItemBinding binding) {
//            super(binding.getRoot());
//            this.binding = binding;
//        }
//
//        public void bind(int color, int position, int selectedPosition) {
//            ColorDrawable imageDrawable = new ColorDrawable(color);
//            binding.imageColor.setImageDrawable(imageDrawable);
//            if (position == selectedPosition) {
//                binding.imageShadow.setVisibility(View.VISIBLE);
//                binding.imagePicked.setVisibility(View.VISIBLE);
//            } else {
//                binding.imageShadow.setVisibility(View.INVISIBLE);
//                binding.imagePicked.setVisibility(View.INVISIBLE);
//            }
//        }
//    }
//
//    private final DiffUtil.ItemCallback<Integer> diffCallback = new DiffUtil.ItemCallback<Integer>() {
//        @Override
//        public boolean areItemsTheSame(@NonNull Integer oldItem, @NonNull Integer newItem) {
//            return oldItem.equals(newItem);
//        }
//
//        @Override
//        public boolean areContentsTheSame(@NonNull Integer oldItem, @NonNull Integer newItem) {
//            return oldItem.equals(newItem);
//        }
//    };
//
//    private final AsyncListDiffer<Integer> differ = new AsyncListDiffer<>(this, diffCallback);
//
//    @NonNull
//    @Override
//    public ColorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        ColorRvItemBinding binding = ColorRvItemBinding.inflate(inflater, parent, false);
//        return new ColorsViewHolder(binding);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ColorsViewHolder holder, int position) {
//        int color = differ.getCurrentList().get(position);
//        holder.bind(color, position, selectedPosition);
//
//        holder.itemView.setOnClickListener(v -> {
//            if (selectedPosition >= 0) {
//                notifyItemChanged(selectedPosition);
//            }
//            selectedPosition = holder.getAdapterPosition();
//            notifyItemChanged(selectedPosition);
//            if (onItemClick != null) {
//                onItemClick.invoke(color);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return differ.getCurrentList().size();
//    }
//
//    private ItemClickListener onItemClick;
//
//    public void setOnItemClickListener(ItemClickListener listener) {
//        this.onItemClick = listener;
//    }
//
//    public interface ItemClickListener {
//        void invoke(int color);
//    }
//}
//
