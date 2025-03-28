package me.erasmusteam.odsmaceerasmusapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import me.erasmusteam.odsmaceerasmusapp.R

class ExpandableListAdapter(
    private val context: Context,
    private val headers: List<String>,
    private val children: HashMap<String, List<String>>,
    private val icons: HashMap<String, Int> // Map headers to icons
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int = headers.size

    override fun getChildrenCount(groupPosition: Int): Int = children[headers[groupPosition]]?.size ?: 0

    override fun getGroup(groupPosition: Int): Any = headers[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Any =
        children[headers[groupPosition]]?.get(childPosition) ?: ""

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = false

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.group_item, parent, false)

        val textView = view.findViewById<TextView>(R.id.groupText)
        val iconView = view.findViewById<ImageView>(R.id.groupIcon)
        val indicatorView = view.findViewById<ImageView>(R.id.groupIndicator)

        val groupTitle = headers[groupPosition]
        textView.text = groupTitle

        // Check if an icon exists for this header
        val iconRes = icons[groupTitle]
        if (iconRes != null) {
            iconView.setImageResource(iconRes)
            iconView.visibility = View.VISIBLE
        } else {
            iconView.visibility = View.GONE // Hide icon if not needed
        }

        // Only show the indicator if the group has children.
        if (getChildrenCount(groupPosition) > 0) {
            indicatorView.visibility = View.VISIBLE
            // Choose the appropriate indicator based on isExpanded
            val indicatorRes = if (isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more
            indicatorView.setImageResource(indicatorRes)
        } else {
            indicatorView.visibility = View.GONE
        }

        println("Group $groupPosition has ${getChildrenCount(groupPosition)} children")

        println("Drawable state of group $groupPosition also known as ${headers[groupPosition]} is ${view.drawableState.contentToString()}")


        return view
    }



    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.expandable_list_simple_item_1, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = getChild(groupPosition, childPosition).toString()

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
}
