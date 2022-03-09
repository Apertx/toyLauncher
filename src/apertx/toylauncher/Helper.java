package apertx.toylauncher;
import android.graphics.*;
import android.graphics.drawable.*;
import android.opengl.*;
import java.util.*;
import java.nio.*;

public class Helper{
 public static Bitmap getBitmap(Drawable drawable){
  final Bitmap bm=Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
  final Canvas canv=new Canvas(bm);
  drawable.setBounds(0,0,canv.getWidth(),canv.getHeight());
  drawable.draw(canv);
  return bm;
 }
 public static Bitmap getIconBitmap(Drawable drawable,int size){
  final Bitmap bm=Bitmap.createBitmap(size,size,Bitmap.Config.ARGB_8888);
  final Canvas canv=new Canvas(bm);
  drawable.setBounds(0,0,size,size);
  Path pth=new Path();
  pth.addRoundRect(new RectF(0.0f,0.0f,size,size),size/5.33f,size/5.33f,Path.Direction.CW);
  canv.clipPath(pth);
  drawable.draw(canv);
  return bm;
 }

 public static Bitmap getAtlas(List<AppDetail>apps,int label_color,int size){
  final int xsize=size+2;
  final int ysize=(int)(size*1.25f)+2;
  final int len=(int)Math.sqrt(apps.size()-1)+1;
  final Bitmap bm=Bitmap.createBitmap(xsize*len,ysize*len,Bitmap.Config.ARGB_8888);
  final Canvas canv=new Canvas(bm);
  final Paint pnt=new Paint();
  pnt.setTextSize(size/8);
  pnt.setAntiAlias(true);
  pnt.setTypeface(Typeface.MONOSPACE);
  for(int i=0;i<len;i++)
   for(int j=0;j<len;j++)
    if(i*len+j<apps.size()){
     final AppDetail app=apps.get(i*len+j);
     canv.drawBitmap(app.icon,j*xsize+1,i*ysize+1,null);
     final String label=app.label.length()<=12?app.label.toString():app.label.toString().substring(0,12);
     final float dx=xsize/2-pnt.measureText(label)/2.0f;
     pnt.setColor(0x7F000000);
     //canv.drawText(label,j*xsize+dx+2,i*ysize+ysize-size/8,pnt);
     pnt.setColor(label_color);
     canv.drawText(label,j*xsize+dx,i*ysize+ysize-size/8,pnt);
    }else break;
  return bm;
 }

 public static void setCoord(List<AppDetail>apps){
  final int len=(int)Math.sqrt(apps.size()-1)+1;
  final float alen=1.0f/len;
  for(int i=0;i<len-1;i++)
   for(int j=0;j<len;j++)
    if(i*len+j<apps.size()){
     final AppDetail app=apps.get(i*len+j);
     final float tt=i*alen;
     final float tb=(i+1)*alen;
     final float tl=j*alen;
     final float tr=(j+1)*alen;
     app.tex_coord=ByteBuffer.allocateDirect(48).order(ByteOrder.nativeOrder()).asFloatBuffer().put(
      new float[]{tr,tt,tl,tt,tr,tb, tl,tb,tr,tb,tl,tt});
     app.tex_coord.position(0);
    }else break;
 }

 public static int loadTexture(Bitmap bm){
  final int id[]=new int[1];
  GLES10.glBindTexture(GLES10.GL_TEXTURE_2D,0);
  GLES10.glGenTextures(1,id,0);
  GLES10.glBindTexture(GLES10.GL_TEXTURE_2D,id[0]);
  GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D,GLES10.GL_TEXTURE_MIN_FILTER,GLES10.GL_NEAREST);
  GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D,GLES10.GL_TEXTURE_MAG_FILTER,GLES10.GL_LINEAR);
  GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D,GLES10.GL_TEXTURE_WRAP_S,GLES10.GL_CLAMP_TO_EDGE);
  GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D,GLES10.GL_TEXTURE_WRAP_T,GLES10.GL_CLAMP_TO_EDGE);
  GLES10.glTexEnvx(GLES10.GL_TEXTURE_ENV,GLES10.GL_TEXTURE_ENV_MODE,GLES10.GL_REPLACE);
  GLUtils.texImage2D(GLES10.GL_TEXTURE_2D,0,bm,0);
  GLES10.glBindTexture(GLES10.GL_TEXTURE_2D,0);
  return id[0];
 }
}
