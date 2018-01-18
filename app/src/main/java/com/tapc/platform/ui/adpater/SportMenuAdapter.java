package com.tapc.platform.ui.adpater;

public class SportMenuAdapter  {
//    private Context context;
//    private List<SportData> list;
//
//    public SportMenuAdapter(Context context, List<SportData> list) {
//        this.context = context;
//        this.list = list;
//    }
//
//    @Override
//    public int getCount() {
//        if (list.size() > 0) {
//            return list.size();
//        } else {
//            return 0;
//        }
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return position;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder = null;
//        if (convertView == null) {
//            holder = new ViewHolder();
//            convertView = LayoutInflater.from(context).inflate(R.layout.sport_menu_item, null);
//            holder.stage = (TextView) convertView.findViewById(R.id.sport_menu_stage);
//            holder.time = (TextView) convertView.findViewById(R.id.sport_menu_time);
//            holder.speed = (TextView) convertView.findViewById(R.id.sport_menu_speed);
//            holder.incline = (TextView) convertView.findViewById(R.id.sport_menu_incline);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//        SportData sportData = list.get(position);
//        if (sportData != null) {
//            holder.stage.setText("" + (position + 1));
//            holder.time.setText(sportData.getTime() + " s");
//            holder.speed.setText(sportData.getSpeed() + " km/h");
//            holder.incline.setText(sportData.getIncline() + " %");
//        } else {
//            holder.stage.setText("" + (position + 1));
//            holder.time.setText("-- s");
//            holder.speed.setText("-- km/h");
//            holder.incline.setText("-- %");
//        }
//        return convertView;
//    }
//
//    class ViewHolder {
//        TextView stage;
//        TextView time;
//        TextView speed;
//        TextView incline;
//    }
//
//    public void sendNotifyDataSetChanged(List<SportData> list) {
//        this.list = list;
//        notifyDataSetChanged();
//    }
}
