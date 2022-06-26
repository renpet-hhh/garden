package ufc.erv.garden.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import ufc.erv.garden.R

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        val states = resources.getStringArray(R.array.estados)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, states)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.stateDisplay)
        autoCompleteTextView.setAdapter(arrayAdapter)

        val cities = resources.getStringArray(R.array.cidades)
        val arrayAdapter2 = ArrayAdapter(this, R.layout.dropdown_item, cities)
        val autoCompleteTextView2 = findViewById<AutoCompleteTextView>(R.id.cityDisplay)
        autoCompleteTextView2.setAdapter(arrayAdapter2)
    }
}