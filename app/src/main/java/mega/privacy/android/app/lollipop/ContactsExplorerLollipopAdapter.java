package mega.privacy.android.app.lollipop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mega.privacy.android.app.DatabaseHandler;
import mega.privacy.android.app.MegaApplication;
import mega.privacy.android.app.MegaContact;
import mega.privacy.android.app.R;
import mega.privacy.android.app.components.RoundedImageView;
import mega.privacy.android.app.utils.Constants;
import mega.privacy.android.app.utils.Util;
import nz.mega.sdk.MegaApiAndroid;
import nz.mega.sdk.MegaApiJava;
import nz.mega.sdk.MegaError;
import nz.mega.sdk.MegaRequest;
import nz.mega.sdk.MegaRequestListenerInterface;
import nz.mega.sdk.MegaUser;


/*
 * Adapter for FilestorageActivity list
 */
public class ContactsExplorerLollipopAdapter extends RecyclerView.Adapter<ContactsExplorerLollipopAdapter.ViewHolderContactsExplorerLollipop> implements OnClickListener {
	
	public static ArrayList<String> pendingAvatars = new ArrayList<String>();
	DatabaseHandler dbH = null;
	
	private class UserAvatarListenerExplorer implements MegaRequestListenerInterface{

		Context context;
		ViewHolderContactsExplorerLollipop holder;
		ContactsExplorerLollipopAdapter adapter;
		
		public UserAvatarListenerExplorer(Context context, ViewHolderContactsExplorerLollipop holder, ContactsExplorerLollipopAdapter adapter) {
			this.context = context;
			this.holder = holder;
			this.adapter = adapter;
		}
		
		@Override
		public void onRequestStart(MegaApiJava api, MegaRequest request) {
			log("onRequestStart()");
		}

		@Override
		public void onRequestFinish(MegaApiJava api, MegaRequest request,
				MegaError e) {
			log("onRequestFinish()");
			if (e.getErrorCode() == MegaError.API_OK){
				
				pendingAvatars.remove(request.getEmail());
				
				if (holder.contactMail.compareTo(request.getEmail()) == 0){
					File avatar = null;
					if (context.getExternalCacheDir() != null){
						avatar = new File(context.getExternalCacheDir().getAbsolutePath(), holder.contactMail + ".jpg");
					}
					else{
						avatar = new File(context.getCacheDir().getAbsolutePath(), holder.contactMail + ".jpg");
					}
					Bitmap bitmap = null;
					if (avatar.exists()){
						if (avatar.length() > 0){
							BitmapFactory.Options bOpts = new BitmapFactory.Options();
							bOpts.inPurgeable = true;
							bOpts.inInputShareable = true;
							bitmap = BitmapFactory.decodeFile(avatar.getAbsolutePath(), bOpts);
							if (bitmap == null) {
								avatar.delete();
							}
							else{
								holder.imageView.setImageBitmap(bitmap);
								holder.initialLetter.setVisibility(View.GONE);
							}
						}
					}
					adapter.notifyDataSetChanged();
				}
			}
		}

		@Override
		public void onRequestTemporaryError(MegaApiJava api,
				MegaRequest request, MegaError e) {
			log("onRequestTemporaryError");
		}

		@Override
		public void onRequestUpdate(MegaApiJava api, MegaRequest request) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	// Listener for item check
	public interface OnItemCheckClickListener {
		public void onItemCheckClick(int position);
	}
		
	private Context mContext;
	MegaApiAndroid megaApi;
	OnItemClickListener mItemClickListener;
	private List<MegaUser> contactsFromMEGA;
//	private List<PhoneContacts> contactsFromPhone;
//	private boolean megaContacts = true;
	
	private OnItemCheckClickListener checkClickListener;
	
	public ContactsExplorerLollipopAdapter(Context context, ArrayList<MegaUser> contactsFromMEGA) {
		if (megaApi == null){
			megaApi = ((MegaApplication) ((Activity)context).getApplication()).getMegaApi();
		}		
		setContext(context);
		this.contactsFromMEGA = contactsFromMEGA;
	}
	
	public void setContext(Context context) {
		mContext = context;
	}
	
	public void setOnItemCheckClickListener(OnItemCheckClickListener listener) {
		this.checkClickListener = listener;
	}
	
	// Set new contacts
	public void setContacts(List<MegaUser> contactsFromMEGA){
		this.contactsFromMEGA = contactsFromMEGA;
		notifyDataSetChanged();
	}
	
	public MegaUser getDocumentAt(int position) 
	{
		if(position < contactsFromMEGA.size())
		{	
			return contactsFromMEGA.get(position);
		}

		return null;
	}
	
	@Override
	public int getItemCount() {

		if (contactsFromMEGA == null) {
			return 0;
		}
		
		return contactsFromMEGA.size();
		
	}

    public MegaUser getItem(int position) {

		if(position < contactsFromMEGA.size())
		{	
			return contactsFromMEGA.get(position);
		}

		return null;
    }

	@Override
    public long getItemId(int position) {
        return position;
    } 
	
	public class ViewHolderContactsExplorerLollipop extends RecyclerView.ViewHolder implements View.OnClickListener{
		TextView contactNameTextView;
		TextView phoneEmailTextView;
		RoundedImageView imageView;
		RelativeLayout contactImageLayout;
		TextView initialLetter;
		long contactId;
		String contactName;
		String contactMail;
		String phoneNumber;
		int currentPosition;
		
		public ViewHolderContactsExplorerLollipop(View itemView) {
			super(itemView);
            itemView.setOnClickListener(this);
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(mItemClickListener != null){
				mItemClickListener.onItemClick(v, getPosition());
			}			
		}
	}
	
	public interface OnItemClickListener {
		   public void onItemClick(View view , int position);
	}
	
	public void SetOnItemClickListener(final OnItemClickListener mItemClickListener){
		this.mItemClickListener = mItemClickListener;
	}
	
	public ViewHolderContactsExplorerLollipop onCreateViewHolder(ViewGroup parentView, int viewType) {
		
	    dbH = DatabaseHandler.getDbHandler(mContext);
		
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
		
		View rowView = inflater.inflate(R.layout.contact_explorer_item, parentView, false);
		ViewHolderContactsExplorerLollipop holder = new ViewHolderContactsExplorerLollipop(rowView);
		
		holder.contactNameTextView = (TextView) rowView.findViewById(R.id.contact_explorer_name);
		holder.phoneEmailTextView = (TextView) rowView.findViewById(R.id.contact_explorer_phone_mail);
//		holder.phoneEmailTextView.setVisibility(View.GONE);
		holder.imageView = (RoundedImageView) rowView.findViewById(R.id.contact_explorer_thumbnail);
		holder.contactImageLayout = (RelativeLayout) rowView.findViewById(R.id.contact_explorer_relative_layout_avatar);
		holder.initialLetter = (TextView) rowView.findViewById(R.id.contact_explorer_initial_letter);
		
		return holder;
		
	}

	@Override
	public void onBindViewHolder(ViewHolderContactsExplorerLollipop holder, int position) {
//		boolean isCheckable = mode == Mode.PICK_FILE;
		

		boolean isCheckable = false;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		MegaUser contact = (MegaUser) getItem(position);
		
		holder.currentPosition = position;
		holder.contactMail = contact.getEmail();
		
//		if (isCheckable) {
//			View checkArea = rowView.findViewById(R.id.checkbox);
//			checkArea.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					checkClickListener.onItemCheckClick(position);
//				}
//			});
//		}
		
		createDefaultAvatar(holder, true);
		
		UserAvatarListenerExplorer listener = new UserAvatarListenerExplorer(mContext, holder, this);
		
		MegaContact contactDB = dbH.findContactByHandle(String.valueOf(contact.getHandle()));
		if(contactDB!=null){
			if(!contactDB.getName().equals("")){
				holder.contactNameTextView.setText(contactDB.getName()+" "+contactDB.getLastName());
			}
			else{
				holder.contactNameTextView.setText(contact.getEmail());
			}
		}
		else{
			log("The contactDB is null: ");
		}
		
		holder.phoneEmailTextView.setText(holder.contactMail);
		
		File avatar = null;
		if (mContext.getExternalCacheDir() != null){
			avatar = new File(mContext.getExternalCacheDir().getAbsolutePath(), holder.contactMail + ".jpg");
		}
		else{
			avatar = new File(mContext.getCacheDir().getAbsolutePath(), holder.contactMail + ".jpg");
		}
		Bitmap bitmap = null;
		if (avatar.exists()){
			if (avatar.length() > 0){
				BitmapFactory.Options bOpts = new BitmapFactory.Options();
				bOpts.inPurgeable = true;
				bOpts.inInputShareable = true;
				bitmap = BitmapFactory.decodeFile(avatar.getAbsolutePath(), bOpts);
				if (bitmap == null) {
					avatar.delete();
					if (mContext.getExternalCacheDir() != null){
						megaApi.getUserAvatar(contact, mContext.getExternalCacheDir().getAbsolutePath() + "/" + contact.getEmail() + ".jpg", listener);
					}
					else{
						megaApi.getUserAvatar(contact, mContext.getCacheDir().getAbsolutePath() + "/" + contact.getEmail() + ".jpg", listener);
					}
				}
				else{
					holder.imageView.setImageBitmap(bitmap);
					holder.initialLetter.setVisibility(View.GONE);
				}
			}
			else{
				if (mContext.getExternalCacheDir() != null){
					megaApi.getUserAvatar(contact, mContext.getExternalCacheDir().getAbsolutePath() + "/" + contact.getEmail() + ".jpg", listener);	
				}
				else{
					megaApi.getUserAvatar(contact, mContext.getCacheDir().getAbsolutePath() + "/" + contact.getEmail() + ".jpg", listener);	
				}			
			}
		}	
		else{
			if (!pendingAvatars.contains(contact.getEmail())){
				pendingAvatars.add(contact.getEmail());
				
				if (mContext.getExternalCacheDir() != null){
					megaApi.getUserAvatar(contact, mContext.getExternalCacheDir().getAbsolutePath() + "/" + contact.getEmail() + ".jpg", listener);
				}
				else{
					megaApi.getUserAvatar(contact, mContext.getCacheDir().getAbsolutePath() + "/" + contact.getEmail() + ".jpg", listener);
				}
			}
		}
	}
	
	public void createDefaultAvatar(ViewHolderContactsExplorerLollipop holder, boolean isMegaContact){
		log("createDefaultAvatar()");
		
		Bitmap defaultAvatar = Bitmap.createBitmap(Constants.DEFAULT_AVATAR_WIDTH_HEIGHT,Constants.DEFAULT_AVATAR_WIDTH_HEIGHT, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(defaultAvatar);
		Paint p = new Paint();
		p.setAntiAlias(true);
		if (isMegaContact){
			p.setColor(mContext.getResources().getColor(R.color.lollipop_primary_color));
		}
		else{
			p.setColor(mContext.getResources().getColor(R.color.color_default_avatar_phone));
		}
		
		int radius; 
        if (defaultAvatar.getWidth() < defaultAvatar.getHeight())
        	radius = defaultAvatar.getWidth()/2;
        else
        	radius = defaultAvatar.getHeight()/2;
        
		c.drawCircle(defaultAvatar.getWidth()/2, defaultAvatar.getHeight()/2, radius, p);
		holder.imageView.setImageBitmap(defaultAvatar);
		
		
		Display display = ((Activity)mContext).getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics ();
	    display.getMetrics(outMetrics);
	    float density  = mContext.getResources().getDisplayMetrics().density;
	    
	    int avatarTextSize = getAvatarTextSize(density);
	    log("DENSITY: " + density + ":::: " + avatarTextSize);
	    if (isMegaContact){
		    if (holder.contactMail != null){
			    if (holder.contactMail.length() > 0){
			    	String firstLetter = holder.contactMail.charAt(0) + "";
			    	firstLetter = firstLetter.toUpperCase(Locale.getDefault());
			    	holder.initialLetter.setVisibility(View.VISIBLE);
			    	holder.initialLetter.setText(firstLetter);
			    	holder.initialLetter.setTextSize(32);
			    	holder.initialLetter.setTextColor(Color.WHITE);
			    }
		    }
	    }
	    else{
	    	if (holder.contactName != null){
	    		if (holder.contactName.length() > 0){
	    			String firstLetter = holder.contactName.charAt(0) + "";
			    	firstLetter = firstLetter.toUpperCase(Locale.getDefault());
			    	holder.initialLetter.setVisibility(View.VISIBLE);
			    	holder.initialLetter.setText(firstLetter);
			    	holder.initialLetter.setTextSize(32);
			    	holder.initialLetter.setTextColor(Color.WHITE);
	    		}
	    	}
	    }
	}
	
	private int getAvatarTextSize (float density){
		float textSize = 0.0f;
		
		if (density > 3.0){
			textSize = density * (DisplayMetrics.DENSITY_XXXHIGH / 72.0f);
		}
		else if (density > 2.0){
			textSize = density * (DisplayMetrics.DENSITY_XXHIGH / 72.0f);
		}
		else if (density > 1.5){
			textSize = density * (DisplayMetrics.DENSITY_XHIGH / 72.0f);
		}
		else if (density > 1.0){
			textSize = density * (72.0f / DisplayMetrics.DENSITY_HIGH / 72.0f);
		}
		else if (density > 0.75){
			textSize = density * (72.0f / DisplayMetrics.DENSITY_MEDIUM / 72.0f);
		}
		else{
			textSize = density * (72.0f / DisplayMetrics.DENSITY_LOW / 72.0f); 
		}
		
		return (int)textSize;
	}
	
	@Override
	public void onClick(View v) {
		log("click!");
	}	

	private static void log(String message) {
		Util.log("ContactsExplorerLollipopAdapter", message);
	}
}
