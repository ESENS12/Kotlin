package kr.esens.expandablerecyclerviewexample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class ExpandableAdapter(
    private val personList: List<Person>
) : RecyclerView.Adapter<ExpandableAdapter.MyViewHolder>() {
    lateinit var itemClickListener : (Person, Int) -> Unit

    inner class MyViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(person: Person) {
            val txtName = itemView.findViewById<TextView>(R.id.txt_name)
            val parentView = itemView.findViewById<ConstraintLayout>(R.id.cl_parent_view)
            val imgPhoto = itemView.findViewById<CircleImageView>(R.id.img_photo)
            val imgMore = itemView.findViewById<ImageButton>(R.id.img_more)
            val layoutExpand = itemView.findViewById<LinearLayout>(R.id.layout_expand)

            txtName.text = person.name
            imgPhoto.setImageResource(person.image)

            parentView.setOnClickListener {
                itemClickListener(person,adapterPosition)
                // 1
                val show = toggleLayout(!person.isExpanded, imgMore, layoutExpand)
                person.isExpanded = show
            }
        }

        private fun toggleLayout(isExpanded: Boolean, view: View, layoutExpand: LinearLayout): Boolean {
            // 2
            ToggleAnimation.toggleArrow(view, isExpanded)
            if (isExpanded) {
                ToggleAnimation.expand(layoutExpand)
            } else {
                ToggleAnimation.collapse(layoutExpand)
            }
            return isExpanded
        }
    }

    fun setOnclickListener(listener : (Person, Int) -> Unit){
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(personList[position])
    }

    override fun getItemCount(): Int {
        return personList.size
    }

}