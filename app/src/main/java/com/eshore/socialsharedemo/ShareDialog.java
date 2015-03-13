package com.eshore.socialsharedemo;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.eshore.socialsharedemo.R;
import com.eshore.socialsharedemo.SocialShare.ShareItem;

/**
 * 
 * 社交分享选择对话框
 * 
 * @author fayuan
 */
public class ShareDialog {
	
	public static void show(Context context, final List<ShareItem> shareItems) {
        View dialogView = View.inflate(context, R.layout.dialog_share_layout, null);
		
        final Dialog dialog = new Dialog(context, R.style.ShareDialog);
        WindowManager.LayoutParams wl = dialog.getWindow().getAttributes();
        wl.x = 0;
        wl.y = -1000;
        wl.gravity = Gravity.BOTTOM;

        dialog.getWindow().setAttributes(wl);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);              
                
        dialogView.findViewById(R.id.dialog_share_close_btn).setOnClickListener(
        		new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        
        GridView grid = (GridView) dialogView.findViewById(R.id.dialog_share_gridview);
        grid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            	ShareItem shareItem = shareItems.get(position);
            	shareItem.onClick(shareItem.id);
            	
                dialog.dismiss();
            }
        });
        
        ShareItemAdapter adapter = new ShareItemAdapter(shareItems, context);       
        grid.setAdapter(adapter);  
        
        dialog.setContentView(dialogView);
        dialog.show();
	}
	
	static class ShareItemAdapter extends BaseAdapter {

		List<ShareItem> shareItems;
		
		Context context;
		
		public ShareItemAdapter(List<ShareItem> shareItems, Context context) {
			this.shareItems = shareItems;
			this.context = context;
		}

		@Override
		public int getCount() {
			return shareItems.size();
		}

		@Override
		public ShareItem getItem(int position) {
			return shareItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ViewHolder holder = null;
	        if (convertView == null) {
	            convertView = LayoutInflater.from(context).inflate(R.layout.dialog_share_item, null);

	            holder = new ViewHolder();
	            convertView.setTag(holder);

	            holder.logo = (ImageView) convertView.findViewById(R.id.dialog_share_item_logo);
	            holder.name = (TextView) convertView.findViewById(R.id.dialog_share_item_title);

	        } else {
	            holder = (ViewHolder) convertView.getTag();
	        }

	        holder.logo.setImageResource(shareItems.get(position).logo);
	        holder.name.setText(shareItems.get(position).name);
	        return convertView;
	    }

	    static class ViewHolder {
	        ImageView logo;
	        TextView name;
	    }	
	}
}
