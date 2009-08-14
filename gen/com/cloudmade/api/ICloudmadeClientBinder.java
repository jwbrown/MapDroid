/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/alex/workspace/MapDroid/src/com/cloudmade/api/ICloudmadeClientBinder.aidl
 */
package com.cloudmade.api;
import java.lang.String;
import android.os.RemoteException;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Binder;
import android.os.Parcel;
import com.cloudmade.api.geocoding.GeoResults;
import com.cloudmade.api.geocoding.GeoResult;
import com.cloudmade.api.routing.Route;
import com.cloudmade.api.geometry.BBox;
import com.cloudmade.api.geometry.Point;
import java.util.List;
/**
 * 
 * @author Alexander Kipar
 *
 */
public interface ICloudmadeClientBinder extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.cloudmade.api.ICloudmadeClientBinder
{
private static final java.lang.String DESCRIPTOR = "com.cloudmade.api.ICloudmadeClientBinder";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an ICloudmadeClientBinder interface,
 * generating a proxy if needed.
 */
public static com.cloudmade.api.ICloudmadeClientBinder asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.cloudmade.api.ICloudmadeClientBinder))) {
return ((com.cloudmade.api.ICloudmadeClientBinder)iin);
}
return new com.cloudmade.api.ICloudmadeClientBinder.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_setApiKey:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setApiKey(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setHost:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
this.setHost(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getTile:
{
data.enforceInterface(DESCRIPTOR);
double _arg0;
_arg0 = data.readDouble();
double _arg1;
_arg1 = data.readDouble();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
int _arg4;
_arg4 = data.readInt();
byte[] _result = this.getTile(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
reply.writeByteArray(_result);
return true;
}
case TRANSACTION_find:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
com.cloudmade.api.geometry.BBox _arg3;
if ((0!=data.readInt())) {
_arg3 = com.cloudmade.api.geometry.BBox.CREATOR.createFromParcel(data);
}
else {
_arg3 = null;
}
boolean _arg4;
_arg4 = (0!=data.readInt());
boolean _arg5;
_arg5 = (0!=data.readInt());
boolean _arg6;
_arg6 = (0!=data.readInt());
com.cloudmade.api.geocoding.GeoResults _result = this.find(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_findClosest:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.cloudmade.api.geometry.Point _arg1;
if ((0!=data.readInt())) {
_arg1 = com.cloudmade.api.geometry.Point.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
com.cloudmade.api.geocoding.GeoResult _result = this.findClosest(_arg0, _arg1);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_route:
{
data.enforceInterface(DESCRIPTOR);
com.cloudmade.api.geometry.Point _arg0;
if ((0!=data.readInt())) {
_arg0 = com.cloudmade.api.geometry.Point.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
com.cloudmade.api.geometry.Point _arg1;
if ((0!=data.readInt())) {
_arg1 = com.cloudmade.api.geometry.Point.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
java.lang.String _arg2;
_arg2 = data.readString();
java.util.List<com.cloudmade.api.geometry.Point> _arg3;
_arg3 = data.createTypedArrayList(com.cloudmade.api.geometry.Point.CREATOR);
java.lang.String _arg4;
_arg4 = data.readString();
java.lang.String _arg5;
_arg5 = data.readString();
java.lang.String _arg6;
_arg6 = data.readString();
com.cloudmade.api.routing.Route _result = this.route(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.cloudmade.api.ICloudmadeClientBinder
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void setApiKey(java.lang.String apiKey) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(apiKey);
mRemote.transact(Stub.TRANSACTION_setApiKey, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setHost(java.lang.String host, int port) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(host);
_data.writeInt(port);
mRemote.transact(Stub.TRANSACTION_setHost, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public byte[] getTile(double latitude, double longitude, int zoom, int styleId, int size) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeDouble(latitude);
_data.writeDouble(longitude);
_data.writeInt(zoom);
_data.writeInt(styleId);
_data.writeInt(size);
mRemote.transact(Stub.TRANSACTION_getTile, _data, _reply, 0);
_reply.readException();
_result = _reply.createByteArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public com.cloudmade.api.geocoding.GeoResults find(java.lang.String query, int results, int skip, com.cloudmade.api.geometry.BBox bbox, boolean bboxOnly, boolean returnGeometry, boolean returnLocation) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.cloudmade.api.geocoding.GeoResults _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(query);
_data.writeInt(results);
_data.writeInt(skip);
if ((bbox!=null)) {
_data.writeInt(1);
bbox.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeInt(((bboxOnly)?(1):(0)));
_data.writeInt(((returnGeometry)?(1):(0)));
_data.writeInt(((returnLocation)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_find, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.cloudmade.api.geocoding.GeoResults.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public com.cloudmade.api.geocoding.GeoResult findClosest(java.lang.String object_type, com.cloudmade.api.geometry.Point point) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.cloudmade.api.geocoding.GeoResult _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(object_type);
if ((point!=null)) {
_data.writeInt(1);
point.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_findClosest, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.cloudmade.api.geocoding.GeoResult.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public com.cloudmade.api.routing.Route route(com.cloudmade.api.geometry.Point start, com.cloudmade.api.geometry.Point end, java.lang.String routeType, java.util.List<com.cloudmade.api.geometry.Point> transitPoints, java.lang.String typeModifier, java.lang.String lang, java.lang.String units) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.cloudmade.api.routing.Route _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((start!=null)) {
_data.writeInt(1);
start.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((end!=null)) {
_data.writeInt(1);
end.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(routeType);
_data.writeTypedList(transitPoints);
_data.writeString(typeModifier);
_data.writeString(lang);
_data.writeString(units);
mRemote.transact(Stub.TRANSACTION_route, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.cloudmade.api.routing.Route.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_setApiKey = (IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_setHost = (IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getTile = (IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_find = (IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_findClosest = (IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_route = (IBinder.FIRST_CALL_TRANSACTION + 5);
}
public void setApiKey(java.lang.String apiKey) throws android.os.RemoteException;
public void setHost(java.lang.String host, int port) throws android.os.RemoteException;
public byte[] getTile(double latitude, double longitude, int zoom, int styleId, int size) throws android.os.RemoteException;
public com.cloudmade.api.geocoding.GeoResults find(java.lang.String query, int results, int skip, com.cloudmade.api.geometry.BBox bbox, boolean bboxOnly, boolean returnGeometry, boolean returnLocation) throws android.os.RemoteException;
public com.cloudmade.api.geocoding.GeoResult findClosest(java.lang.String object_type, com.cloudmade.api.geometry.Point point) throws android.os.RemoteException;
public com.cloudmade.api.routing.Route route(com.cloudmade.api.geometry.Point start, com.cloudmade.api.geometry.Point end, java.lang.String routeType, java.util.List<com.cloudmade.api.geometry.Point> transitPoints, java.lang.String typeModifier, java.lang.String lang, java.lang.String units) throws android.os.RemoteException;
}
