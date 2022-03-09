package apertx.toylauncher;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.opengl.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import java.nio.*;
import java.util.*;
import javax.microedition.khronos.opengles.*;
import java.io.*;

public class MainActivity extends Activity implements GLSurfaceView.Renderer,OnTouchListener{
 protected void onCreate(Bundle b){
  super.onCreate(b);
  glsv=new GLSurfaceView(this);this.
  glsv.setEGLConfigChooser(false);
  glsv.setRenderer(this);
  glsv.setOnTouchListener(this);
  glsv.setRenderMode(glsv.RENDERMODE_WHEN_DIRTY);
  setContentView(glsv);
  vert=ByteBuffer.allocateDirect(12).put(new byte[]{1,1,-1,1,1,-1,-1,-1,1,-1,-1,1});
  vert.position(0);
  pref=getSharedPreferences("me",0);
  apps=new ArrayList<AppDetail>();
  init();
 }

 public void onSurfaceCreated(GL10 gl,javax.microedition.khronos.egl.EGLConfig conf){
  gl.glActiveTexture(gl.GL_TEXTURE0);
  gl.glVertexPointer(2,gl.GL_BYTE,0,vert);
  gl.glEnableClientState(gl.GL_VERTEX_ARRAY);
  gl.glEnableClientState(gl.GL_TEXTURE_COORD_ARRAY);

  gl.glBlendFunc(gl.GL_SRC_ALPHA,gl.GL_ONE_MINUS_SRC_ALPHA);
  gl.glHint(gl.GL_PERSPECTIVE_CORRECTION_HINT,gl.GL_FASTEST);
  gl.glShadeModel(gl.GL_FLAT);
  gl.glDepthMask(false);
  gl.glDisable(gl.GL_DITHER);
  gl.glEnable(gl.GL_TEXTURE_2D);
  gl.glEnable(gl.GL_BLEND);
  gl.glEnable(gl.GL_CULL_FACE);
  tex_tex=Helper.loadTexture(icons);
  gl.glClearColorx(0x7FFF,0x7FFF,0x7FFF,0xFFFF);
 }
 public void onSurfaceChanged(GL10 gl,int w,int h){
  gl.glViewport(0,0,w,h);
  win_width=w;
  win_height=h;
  win_ratio=(float)w/h;
  if(win_ratio>1.0f){
   tex_xscale=page_icon_size/win_ratio;
   tex_yscale=page_icon_size*1.2424f;
  }else{
   tex_xscale=page_icon_size;
   tex_yscale=page_icon_size*1.2424f*win_ratio;
  }
 }
 public void onDrawFrame(GL10 gl){
  gl.glClear(gl.GL_COLOR_BUFFER_BIT);
  gl.glBindTexture(gl.GL_TEXTURE_2D,tex_tex);
  for(int i=0;i<apps.size();i++){
   gl.glTexCoordPointer(2,gl.GL_FLOAT,0,apps.get(i).tex_coord);
   gl.glLoadIdentity();
   gl.glTranslatef(apps.get(i).x,apps.get(i).y,0.0f);
   gl.glTranslatef(page_x-page_offset*2.0f,0.0f,0.0f);
   gl.glScalef(tex_xscale,tex_yscale,1.0f);
   gl.glDrawArrays(gl.GL_TRIANGLES,0,6);
  }
 }

 public boolean onTouch(View v,MotionEvent e){
  switch(e.getAction()){
   case e.ACTION_DOWN:
    tap_moved=false;
    tap_time=SystemClock.uptimeMillis();
    tap_x=e.getX();
    tap_y=e.getY();
    break;
   case e.ACTION_UP:
    if(tap_moved){
     if(page_x>0.125f&&page_offset>0)page_offset-=1;
     if(page_x<-0.125f&&page_offset<pages-1)page_offset+=1;
     page_x=0.0f;
     glsv.requestRender();
    }else{
     if(SystemClock.uptimeMillis()-tap_time<300){
      float tapX=e.getX()/win_width;
      float tapY=e.getY()/win_height;
      int zoneX=(int)((tapX-0.5f/(page_width+1))*(page_width+1));
      int zoneY=(int)((tapY-0.5f/(page_height+1))*(page_height+1));
      int pos=page_offset*ipage+zoneY*page_width+zoneX;
      if(zoneX>=0&&zoneY>=0&&zoneX<page_width&&zoneY<page_height&&pos<apps.size())
       startActivity(apps.get(pos).name);
     }
    }
    break;
   case e.ACTION_MOVE:
    if(tap_moved){
     page_x=2.0f*(e.getX()-tap_x)/win_width;
     if(page_x>0.5f&&page_offset==0)page_x=0.5f;
     if(page_x<-0.5f&&page_offset==pages-1)page_x=-0.5f;
     glsv.requestRender();
    }else if(Math.abs(e.getX()-tap_x)>4&&Math.abs(e.getY()-tap_y)>4){
     tap_moved=true;
     tap_x=e.getX();
     tap_y=e.getY();
    }
    break;
  }
  return true;
 }

 public void onBackPressed(){
  if(SystemClock.uptimeMillis()-back_time<300){
   init();
   if(win_ratio>1.0f){
    tex_xscale=page_icon_size/win_ratio;
    tex_yscale=page_icon_size*1.2424f;
   }else{
    tex_xscale=page_icon_size;
    tex_yscale=page_icon_size*1.2424f*win_ratio;
   }
  }else back_time=SystemClock.uptimeMillis();
 }
 protected void onResume(){
  super.onResume();
  glsv.onResume();
 }
 protected void onPause(){
  glsv.onPause();
  super.onPause();
 }

 private void init(){
  apps.clear();
  PackageManager manager=getPackageManager();
  Intent intent_filter=new Intent(Intent.ACTION_MAIN,null).addCategory(Intent.CATEGORY_LAUNCHER);
  List<ResolveInfo>availableActivities=manager.queryIntentActivities(intent_filter,0);
  for(ResolveInfo ri:availableActivities){
   AppDetail app=new AppDetail();
   app.name=manager.getLaunchIntentForPackage(ri.activityInfo.packageName);
   app.label=ri.loadLabel(manager);
   app.icon=Helper.getIconBitmap(ri.loadIcon(manager),192);
   apps.add(app);
  }
  icons=Helper.getAtlas(apps,pref.getInt("label_color",0xFFFFFFFF),192);
  page_width=pref.getInt("grid_width",4);
  page_height=pref.getInt("grid_height",6);
  page_xsize=2.0f/(page_width+1.0f);
  page_xoff=1.0f-page_xsize;
  page_ysize=2.0f/(page_height+1.0f);
  page_yoff=1.0f-page_ysize;
  page_icon_size=page_xsize>page_ysize?page_ysize*0.5f:page_xsize*0.5f;
  ipage=page_width*page_height;
  pages=(apps.size()-1)/(ipage)+1;
  page_offset=0;
  Helper.setCoord(apps);
  for(int k=0;k<pages;k++)
   for(int j=0;j<page_height;j++){
    int ii=k*ipage+j*page_width;
    for(int i=0;i<page_width;i++)
     if(ii+i<apps.size()){
      apps.get(ii+i).x=i*page_xsize-page_xoff+k*2.0f;
      apps.get(ii+i).y=page_yoff-j*page_ysize;
     }else break;
   }
  glsv.requestRender();
 }
 private List<AppDetail>apps;
 private GLSurfaceView glsv;
 private SharedPreferences pref;
 private ByteBuffer vert;
 private Bitmap icons;
 private int tex_tex;
 private float tex_xscale;
 private float tex_yscale;
 private int page_offset;
 private float page_x;
 private int page_width;
 private int page_height;
 private float page_xoff;
 private float page_yoff;
 private float page_xsize;
 private float page_ysize;
 private float page_icon_size;
 private int pages;
 private int ipage;
 private float win_ratio;
 private int win_width;
 private int win_height;
 private boolean tap_moved;
 private long tap_time;
 private float tap_x;
 private float tap_y;
 private long back_time;
}
