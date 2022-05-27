package ufc.erv.garden.util

import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView

// https://stackoverflow.com/questions/35224459/how-to-detect-if-users-stop-typing-in-edittext-android
fun TextView.onTextFinished(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        var timer: CountDownTimer? = null

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(editable: Editable?) {
            timer?.cancel() // cancels timer if the user types before the countdown finishes
            timer = object : CountDownTimer(1000, 1500) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    afterTextChanged.invoke(editable.toString())
                }
            }.start()
        }
    })
}