package com.lee.toollibrary.utils;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;

/**
 *
 * @author nicklxz
 * @date 2017/11/7
 * 获取手机通讯录信息
 */

public class ContactUtil {
//    Context mContext = null;

    /**获取库Phon表字段**/
    private static final String[] PHONES_PROJECTION = new String[] {
            Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };

    /**联系人显示名称**/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**电话号码**/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**头像ID**/
    private static final int PHONES_PHOTO_ID_INDEX = 2;

    /**联系人的ID**/
    private static final int PHONES_CONTACT_ID_INDEX = 3;

//    private static  List<LocalFriendBean> list=new ArrayList<>();
    /**联系人名称**/
//    private static  ArrayList<String> mContactsName = new ArrayList<String>();

    /**联系人头像**/
//    private static ArrayList<String> mContactsNumber = new ArrayList<String>();
//    static   LocalFriendBean bean;
//    /**联系人头像**/
//    private static ArrayList<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();

    private static ContactUtil instance;
    public static ContactUtil getInstance(){
        if (instance==null){
            instance=new ContactUtil();
        }else {

        }
        return instance;
    }
    private ContactUtil() {
        if (instance==null){
            instance=this;}
    }

    /**得到手机通讯录联系人信息**/
    public  void  getPhoneContacts(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();
        // 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null) {

//            list=new ArrayList<>();
            while (phoneCursor.moveToNext()) {
//                bean=new LocalFriendBean();
                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                phoneNumber.replaceAll(" ","");

                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber)) {
                    continue;
                }

                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

//                LogUtils.eLoger(contactName);
                //得到联系人ID
                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
//                bean.setName(contactName);
//                bean.setPhoneNumber(phoneNumber.replaceAll(" ",""));
//                list.add(bean);
                //得到联系人头像ID
//                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

                //得到联系人头像Bitamp
//                Bitmap contactPhoto = null;

//                //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
//                if(photoid > 0 ) {
//                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactid);
//                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
//                    contactPhoto = BitmapFactory.decodeStream(input);
//                }else {
//                    contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.contact_photo);
//                }

//                mContactsName.add(contactName);
//                mContactsNumber.add(phoneNumber);
//                mContactsPhonto.add(contactPhoto);

            }

            phoneCursor.close();

//            if (listener!=null){
//                listener.onFinish(list);
//            }
       }
    }
//    private LoadContactListener listener;
//    public void setListener(LoadContactListener loadContactListener){
//        listener=loadContactListener;
//    }
}
