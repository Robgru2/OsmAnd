package net.osmand.plus.settings;

import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.osmand.map.ITileSource;
import net.osmand.plus.ApplicationMode;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.R;
import net.osmand.plus.UiUtilities;
import net.osmand.plus.activities.OsmandBaseExpandableListAdapter;
import net.osmand.plus.poi.PoiUIFilter;
import net.osmand.plus.profiles.AdditionalDataWrapper;
import net.osmand.plus.quickaction.QuickAction;
import net.osmand.plus.render.RenderingIcons;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class ExportImportSettingsAdapter extends OsmandBaseExpandableListAdapter {

	private OsmandApplication app;
	private List<? super Object> dataToOperate;
	private List<AdditionalDataWrapper> dataList;
	private boolean nightMode;
	private boolean importState;
	private int profileColor;

	ExportImportSettingsAdapter(OsmandApplication app, List<AdditionalDataWrapper> dataList, boolean nightMode, boolean importState) {
		this.app = app;
		this.dataList = dataList;
		this.nightMode = nightMode;
		this.importState = importState;
		this.dataToOperate = new ArrayList<>();
		this.profileColor = app.getSettings().getApplicationMode().getIconColorInfo().getColor(nightMode);
		if (!importState) {
			for (AdditionalDataWrapper dataWrapper : dataList) {
				dataToOperate.addAll(dataWrapper.getItems());
			}
		}
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View group = convertView;
		if (group == null) {
			LayoutInflater inflater = UiUtilities.getInflater(app, nightMode);
			group = inflater.inflate(R.layout.profile_data_list_item_group, parent, false);
		}

		boolean isLastGroup = groupPosition == getGroupCount() - 1;
		final AdditionalDataWrapper.Type type = dataList.get(groupPosition).getType();

		TextView titleTv = group.findViewById(R.id.title_tv);
		TextView subTextTv = group.findViewById(R.id.sub_text_tv);
		final CheckBox checkBox = group.findViewById(R.id.check_box);
		ImageView expandIv = group.findViewById(R.id.explist_indicator);
		View lineDivider = group.findViewById(R.id.divider);
		View cardTopDivider = group.findViewById(R.id.card_top_divider);
		View cardBottomDivider = group.findViewById(R.id.card_bottom_divider);

		titleTv.setText(getGroupTitle(type));
		lineDivider.setVisibility(importState || isExpanded || isLastGroup ? View.GONE : View.VISIBLE);
		cardTopDivider.setVisibility(importState ? View.VISIBLE : View.GONE);
		cardBottomDivider.setVisibility(importState && !isExpanded ? View.VISIBLE : View.GONE);
		CompoundButtonCompat.setButtonTintList(checkBox, ColorStateList.valueOf(ContextCompat.getColor(app, profileColor)));

		final List<?> listItems = dataList.get(groupPosition).getItems();
		subTextTv.setText(String.valueOf(listItems.size()));

		checkBox.setChecked(dataToOperate.containsAll(listItems));
		checkBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (checkBox.isChecked()) {
					for (Object object : listItems) {
						if (!dataToOperate.contains(object)) {
							dataToOperate.add(object);
						}
					}
				} else {
					dataToOperate.removeAll(listItems);
				}
				notifyDataSetChanged();
			}
		});
		adjustIndicator(app, groupPosition, isExpanded, group, true);
		return group;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		View child = convertView;
		if (child == null) {
			LayoutInflater inflater = UiUtilities.getInflater(app, nightMode);
			child = inflater.inflate(R.layout.profile_data_list_item_child, parent, false);
		}
		final Object currentItem = dataList.get(groupPosition).getItems().get(childPosition);

		boolean isLastGroup = groupPosition == getGroupCount() - 1;
		final AdditionalDataWrapper.Type type = dataList.get(groupPosition).getType();

		TextView title = child.findViewById(R.id.title_tv);
		TextView subText = child.findViewById(R.id.sub_title_tv);
		final CheckBox checkBox = child.findViewById(R.id.check_box);
		ImageView icon = child.findViewById(R.id.icon);
		View lineDivider = child.findViewById(R.id.divider);
		View cardBottomDivider = child.findViewById(R.id.card_bottom_divider);

		lineDivider.setVisibility(!importState && isLastChild && !isLastGroup ? View.VISIBLE : View.GONE);
		cardBottomDivider.setVisibility(importState && isLastChild ? View.VISIBLE : View.GONE);
		CompoundButtonCompat.setButtonTintList(checkBox, ColorStateList.valueOf(ContextCompat.getColor(app, profileColor)));

		checkBox.setChecked(dataToOperate.contains(currentItem));
		checkBox.setClickable(false);
		child.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (dataToOperate.contains(currentItem)) {
					dataToOperate.remove(currentItem);
				} else {
					dataToOperate.add(currentItem);
				}
				notifyDataSetChanged();
			}
		});

		switch (type) {
			case PROFILE:
				title.setText(((ApplicationMode) currentItem).toHumanString());
				subText.setText(((ApplicationMode) currentItem).getDescription());
				icon.setImageDrawable(app.getUIUtilities().getIcon(((ApplicationMode) currentItem).getIconRes(), profileColor));
				subText.setVisibility(View.VISIBLE);
				icon.setVisibility(View.VISIBLE);
				break;
			case QUICK_ACTIONS:
				title.setText(((QuickAction) currentItem).getName(app.getApplicationContext()));
				icon.setImageDrawable(app.getUIUtilities().getIcon(((QuickAction) currentItem).getIconRes(), nightMode));
				subText.setVisibility(View.GONE);
				icon.setVisibility(View.VISIBLE);
				break;
			case POI_TYPES:
				title.setText(((PoiUIFilter) currentItem).getName());
				int iconRes = RenderingIcons.getBigIconResourceId(((PoiUIFilter) currentItem).getIconId());
				icon.setImageDrawable(app.getUIUtilities().getIcon(iconRes != 0 ? iconRes : R.drawable.ic_person, profileColor));
				subText.setVisibility(View.GONE);
				icon.setVisibility(View.VISIBLE);
				break;
			case MAP_SOURCES:
				title.setText(((ITileSource) currentItem).getName());
				icon.setImageResource(R.drawable.ic_action_info_dark);
				subText.setVisibility(View.GONE);
				icon.setVisibility(View.INVISIBLE);
				break;
			case CUSTOM_RENDER_STYLE:
				String renderName = ((File) currentItem).getName();
				renderName = renderName.replace('_', ' ').replaceAll(".render.xml", "");
				title.setText(renderName);
				icon.setImageResource(R.drawable.ic_action_info_dark);
				icon.setVisibility(View.INVISIBLE);
				subText.setVisibility(View.GONE);
				break;
			case CUSTOM_ROUTING:
				String routingName = ((File) currentItem).getName();
				routingName = routingName.replace('_', ' ').replaceAll(".xml", "");
				title.setText(routingName);
				icon.setImageResource(R.drawable.ic_action_map_style);
				icon.setVisibility(View.VISIBLE);
				subText.setVisibility(View.GONE);
				break;
			default:
				return child;
		}
		return child;
	}

	@Override
	public int getGroupCount() {
		return dataList.size();
	}

	@Override
	public int getChildrenCount(int i) {
		return dataList.get(i).getItems().size();
	}

	@Override
	public Object getGroup(int i) {
		return dataList.get(i);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return dataList.get(groupPosition).getItems().get(childPosition);
	}

	@Override
	public long getGroupId(int i) {
		return i;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return groupPosition * 10000 + childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int i, int i1) {
		return true;
	}

	private int getGroupTitle(AdditionalDataWrapper.Type type) {
		switch (type) {
			case PROFILE:
				return R.string.shared_sting_profiles;
			case QUICK_ACTIONS:
				return R.string.configure_screen_quick_action;
			case POI_TYPES:
				return R.string.poi_dialog_poi_type;
			case MAP_SOURCES:
				return R.string.quick_action_map_source_title;
			case CUSTOM_RENDER_STYLE:
				return R.string.shared_string_custom_rendering_style;
			case CUSTOM_ROUTING:
				return R.string.shared_string_routing;
			default:
				return R.string.access_empty_list;
		}
	}

	public void updateSettingsList(List<AdditionalDataWrapper> settingsList) {
		this.dataList = settingsList;
		notifyDataSetChanged();
	}

	public void selectAll(boolean selectAll) {
		dataToOperate.clear();
		if (selectAll) {
			for (AdditionalDataWrapper item : dataList) {
				dataToOperate.addAll(item.getItems());
			}
		}
		notifyDataSetChanged();
	}

	List<? super Object> getDataToOperate() {
		return this.dataToOperate;
	}
}
