package com.example.babyneeds.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.example.babyneeds.R;
import com.example.babyneeds.data.DatabaseHandler;
import com.example.babyneeds.model.Item;
import com.google.android.material.snackbar.Snackbar;

import java.text.MessageFormat;
import java.util.List;

public class RecyclerViewAdapter extends Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Item> itemList;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;




    public RecyclerViewAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row, viewGroup, false);




        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Item item = itemList.get(position); //object item

        viewHolder.itemName.setText(MessageFormat.format("Item: {0}", item.getItemName()));
        viewHolder.itemColor.setText(MessageFormat.format("Color: {0}", item.getItemColor()));
        viewHolder.quantity.setText(MessageFormat.format("Qty: {0}", String.valueOf(item.getItemQuantity())));
        viewHolder.size.setText(MessageFormat.format("Size: {0}", String.valueOf(item.getItemSize())));
        viewHolder.dateAdded.setText(MessageFormat.format("Added on: {0}", item.getDateItemAdded()));

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView itemName;
        public TextView itemColor;
        public TextView quantity;
        public TextView size;
        public TextView dateAdded;
        public Button editButton;
        public Button deleteButton;

        public int id;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            itemName = itemView.findViewById(R.id.item_name);
            itemColor = itemView.findViewById(R.id.item_color);
            quantity = itemView.findViewById(R.id.item_quantity);
            size = itemView.findViewById(R.id.item_size);
            dateAdded = itemView.findViewById(R.id.item_date);

            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);



        }

        @Override
        public void onClick(View v) {

            int position;
            position = getAdapterPosition();
            Item item = itemList.get(position);

            switch (v.getId()) {
                case R.id.edit_button:
                    //edit item

                    editItem(item);
                    break;
                case R.id.deleteButton:
                    //delete item

                    deleteItem(item.getId());
                    break;

            }
        }

        private void deleteItem(final int id) {

            builder = new AlertDialog.Builder(context);

            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confirmation_pop, null);

            Button noButton = view.findViewById(R.id.conf_no_button);

            Button yesButton = view.findViewById(R.id.conf_yes_button);

            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    db.deleteItem(id);
                    itemList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                    dialog.dismiss();

                }
            });

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });




        }
    }





    private void editItem(final Item newItem) {

        builder = new AlertDialog.Builder(context);
        inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout. popup, null);

        Button saveButton;
        final EditText babyItem;
        final EditText itemQuantity;
        final EditText itemColor;
        final EditText itemSize;
        TextView title;

        babyItem = view.findViewById(R.id.babyItem);
        itemQuantity = view.findViewById(R.id.itemQuantity);
        itemColor = view.findViewById(R.id.itemColor);
        itemSize = view.findViewById(R.id.itemSize);
        saveButton = view.findViewById(R.id.saveButton);
        title = view.findViewById(R.id.title);

        title.setText(R.string.edit_item);
        babyItem.setText(newItem.getItemName());
        itemQuantity.setText(String.valueOf(newItem.getItemQuantity()));
        itemColor.setText(newItem.getItemColor());
        itemSize.setText(String.valueOf(newItem.getItemSize()));

        builder.setView(view);
        dialog = builder.create();

        dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {


                DatabaseHandler databaseHandler = new DatabaseHandler(context);

                //update items
                newItem.setItemName(babyItem.getText().toString());
                newItem.setItemColor(itemColor.getText().toString());
                newItem.setItemQuantity(Integer.parseInt(itemQuantity.getText().toString()));
                newItem.setItemSize(Integer.parseInt(itemSize.getText().toString()));

                if (!babyItem.getText().toString().isEmpty()
                        && !itemColor.getText().toString().isEmpty()
                        && !itemQuantity.getText().toString().isEmpty()
                        && !itemSize.getText().toString().isEmpty()) {




                    databaseHandler.updateItem(newItem);
                    //notifyItemChanged(getAdapterPosition(), newItem); //important!

                } else {
                    Snackbar.make(view, "Fields Empty",
                            Snackbar.LENGTH_SHORT)
                            .show();
                }

                dialog.dismiss();


            }


        });



    }




}
