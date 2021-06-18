package com.example.chatme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        viewPager.adapter = ScreenSliderAdapter(this)
        TabLayoutMediator(tabs,viewPager, TabLayoutMediator.TabConfigurationStrategy{ tab: TabLayout.Tab, pos: Int ->
            when(pos){
                0 -> tab.text= "CHATS"
                1 -> tab.text = "PEOPLE"
                2 -> tab.text = "NEWS"
            }
        }).attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu,menu)

      /*  val item = menu?.findItem(R.id.search)
        val searchView = item?.actionView as SearchView

        item.setOnActionExpandListener(object :MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                displayInfo()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                displayInfo()
                return true
            }

        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
               return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(!newText.isNullOrEmpty()){
                    displayInfo(newText)
                }
                return true
            }

        })*/


        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        
    }


}