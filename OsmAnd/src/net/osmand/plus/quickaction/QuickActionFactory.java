package net.osmand.plus.quickaction;


import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.osmand.plus.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuickActionFactory {

    public String quickActionListToString(ArrayList<QuickAction> quickActions){

        String json = new Gson().toJson(quickActions);
        return json;
    }

    public List<QuickAction> parseActiveActionsList(String json) {

        Type type = new TypeToken<List<QuickAction>>(){}.getType();
        ArrayList<QuickAction> quickActions = new Gson().fromJson(json, type);

        return quickActions != null ? quickActions : new ArrayList<QuickAction>();
    }

    public static List<QuickAction> produceTypeActionsList() {

        ArrayList<QuickAction> quickActions = new ArrayList<>();

        quickActions.add(new MarkerAction());
        quickActions.add(new FavoriteAction());

        return quickActions;
    }

    public QuickAction produceAction(QuickAction quickAction){

        if (quickAction.id == NewAction.ID) {

            return new NewAction(quickAction);

        } else if (quickAction.id == MarkerAction.ID) {

            return new MarkerAction(quickAction);

        } else if (quickAction.id == FavoriteAction.ID){

            return new FavoriteAction(quickAction);

        } else return quickAction;
    }

    public static class NewAction extends QuickAction {

        public static final int ID = 1;

        protected NewAction(){
            id = ID;
            nameRes = R.string.quick_action_new_action;
            iconRes = R.drawable.ic_action_plus;
        }

        public NewAction(QuickAction quickAction) {
            super(quickAction);
        }

        @Override
        public void execute() {

            //TODO do some action
        }

        @Override
        public void drawUI(ViewGroup parent) {

            //TODO inflate view & fill with params
        }
    }

    public static class MarkerAction extends QuickAction {

        public static final int ID = 2;

        private MarkerAction(){
            id = ID;
            nameRes = R.string.quick_action_add_marker;
            iconRes = R.drawable.ic_action_flag_dark;
        }

        public MarkerAction(QuickAction quickAction) {
            super(quickAction);
        }

        @Override
        public void execute() {

            //TODO do some action
        }

        @Override
        public void drawUI(ViewGroup parent) {

            //TODO inflate view & fill with params
        }
    }

    public static class FavoriteAction extends QuickAction {

        public static final int ID = 3;

        public FavoriteAction() {
            id = ID;
            nameRes = R.string.quick_action_add_favorite;
            iconRes = R.drawable.ic_action_fav_dark;
        }

        public FavoriteAction(QuickAction quickAction) {
            super(quickAction);
        }

        @Override
        public void execute() {

            //TODO do some action
        }

        @Override
        public void drawUI(ViewGroup parent) {

            //TODO inflate view & fill with params
        }
    }
}
