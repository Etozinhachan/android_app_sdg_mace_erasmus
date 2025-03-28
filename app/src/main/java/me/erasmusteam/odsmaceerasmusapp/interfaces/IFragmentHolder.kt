package me.erasmusteam.odsmaceerasmusapp.interfaces

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import me.erasmusteam.odsmaceerasmusapp.R

interface IFragmentHolder {

    public fun addFragmentToActivity(fragment_container_id: Int, fragment: Fragment?, supportFragmentManager: FragmentManager){

        if (fragment == null) return
        val fm = supportFragmentManager
        val tr = fm.beginTransaction()
        tr.add(fragment_container_id, fragment)
        tr.commit()
    }

}