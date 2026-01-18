package com.owezy.app.ui.expenses;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.owezy.app.R;
import com.owezy.app.database.entities.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ViewHolder> {

    private List<Member> members;
    private List<Member> selectedMembers = new ArrayList<>();
    private Map<Long, Double> customAmounts = new HashMap<>();
    private boolean isCustomSplit = false;

    public ParticipantsAdapter(List<Member> members) {
        this.members = members;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_participant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Member member = members.get(position);
        holder.tvParticipantName.setText(member.getName());
        holder.cbParticipant.setOnCheckedChangeListener(null);
        holder.cbParticipant.setChecked(selectedMembers.contains(member));
        holder.etCustomAmount.setVisibility(isCustomSplit && selectedMembers.contains(member) ? View.VISIBLE : View.GONE);

        holder.cbParticipant.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedMembers.contains(member)) {
                    selectedMembers.add(member);
                }
            } else {
                selectedMembers.remove(member);
            }
            notifyItemChanged(position);
        });

        holder.etCustomAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isCustomSplit && selectedMembers.contains(member)) {
                    try {
                        customAmounts.put(member.getId(), Double.parseDouble(s.toString()));
                    } catch (NumberFormatException e) {
                        customAmounts.put(member.getId(), 0.0);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public List<Member> getSelectedMembers() {
        return selectedMembers;
    }

    public Map<Long, Double> getCustomAmounts() {
        return customAmounts;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
        selectedMembers.clear();
        customAmounts.clear();
        notifyDataSetChanged();
    }

    public void setCustomSplit(boolean isCustomSplit) {
        this.isCustomSplit = isCustomSplit;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbParticipant;
        TextView tvParticipantName;
        EditText etCustomAmount;

        ViewHolder(View itemView) {
            super(itemView);
            cbParticipant = itemView.findViewById(R.id.cb_participant);
            tvParticipantName = itemView.findViewById(R.id.tv_participant_name);
            etCustomAmount = itemView.findViewById(R.id.et_custom_amount);
        }
    }
}
