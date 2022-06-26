package ufc.erv.garden.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import ufc.erv.garden.R

class RegisterUserActivity : AppCompatActivity() {
    /*override fun onResume() {
        super.onResume()
    } */


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        val states = resources.getStringArray(R.array.estados)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, states)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.stateEdit)
        autoCompleteTextView.setAdapter(arrayAdapter)

        val cities = resources.getStringArray(R.array.cidades)
        val arrayAdapter2 = ArrayAdapter(this, R.layout.dropdown_item, cities)
        val autoCompleteTextView2 = findViewById<AutoCompleteTextView>(R.id.cityEdit)
        autoCompleteTextView2.setAdapter(arrayAdapter2)
    }
}