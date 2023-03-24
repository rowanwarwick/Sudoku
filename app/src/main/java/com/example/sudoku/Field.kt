package com.example.sudoku

import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.drawable.ColorDrawable
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*

class Field(
    resources: Resources,
    private val massiveEnterNumber: List<TextView>,
    private val variants: List<TextView>
) {
    private lateinit var initialData: MutableList<Int>
    private lateinit var yourNumbers: MutableMap<Int, String>
    private var modeManual = false
    private var lastEnterElement: TextView? = null
    private val pinkColor = resources.getColor(R.color.pink, null)
    private val whiteColor = resources.getColor(R.color.white, null)
    private val checkColor = resources.getColor(R.color.enter, null)
    private val blackColor = resources.getColor(R.color.black, null)
    private val enterColor = resources.getColor(R.color.yourNumber, null)
    private val answerColor = resources.getColor(R.color.answer, null)

    fun enterNumber(view: TextView) {
        lastEnterElement?.setBackgroundColor(whiteColor)
        variants.forEach { it.setBackgroundColor(whiteColor) }
        val indexCell = massiveEnterNumber.indexOf(view)
        view.setBackgroundColor(checkColor)
        lastEnterElement = view
        if (lastEnterElement!!.currentTextColor == blackColor && lastEnterElement!!.text != "") {
            variants.forEach { it.setBackgroundColor(pinkColor) }
        } else {
            for (number in 1..9) {
                if (!search(indexCell, number)) variants[number - 1].setBackgroundColor(pinkColor)
            }
        }
    }

    private fun search(indexCell: Int, number: Int, showNumber: Boolean = true): Boolean {
        var result = true
        val column = indexCell / 9
        val row = indexCell % 9
        loop@ for (elemInRange in 0..8) {
            val indexElemInColumn = elemInRange + 9 * column
            val indexElemInRow = row + 9 * elemInRange
            val indexElemInBox =
                27 * (column / 3) + 3 * (row / 3) + 9 * (elemInRange / 3) + elemInRange % 3
            for (option in listOf(indexElemInColumn, indexElemInRow, indexElemInBox)) {
                if (option != indexCell && number == if (showNumber) (massiveEnterNumber[option].text.toString()
                        .toIntOrNull()) else initialData[option]
                ) {
                    result = false
                    break@loop
                }
            }
        }
        return result
    }

    fun chooseNumber(view: TextView) {
        val drawable = view.background as ColorDrawable
        if (drawable.color != pinkColor && (lastEnterElement?.currentTextColor != blackColor || lastEnterElement?.text == "")) {
            lastEnterElement?.text = view.text
            lastEnterElement?.setTextColor(if (!modeManual) enterColor else blackColor)
        }
    }

    fun switchMode(view: Button) {
        modeManual = !modeManual
        view.text = if (modeManual) "mode: on" else "mode: off"
    }

    private suspend fun makeField(index: Int): Boolean {
        var result = false
        coroutineScope {
            launch {
                if (index > 80) {
                    result = true
                } else {
                    if (initialData[index] != 0) {
                        if (makeField(index + 1)) {
                            result = true
                        }
                    } else {
                        var digit = 0
                        val range = (1..9).shuffled()
                        while (!result && digit in 0..8) {
                            if (search(index, range[digit], false)) {
                                initialData[index] = range[digit]
                                if (makeField(index + 1)) {
                                    result = true
                                }
                                if (!result) initialData[index] = 0
                            }
                            digit++
                        }
                    }
                }
            }
        }
        return result
    }

    private fun openSomeCells(count: Int, forAnswer: Boolean = false) {
        val orderElements = MutableList(81) { it }.shuffled().subList(0, count)
        for (index in orderElements) {
            if (forAnswer && massiveEnterNumber[index].text != initialData[index].toString()) {
                massiveEnterNumber[index].setTextColor(answerColor)
            }
            massiveEnterNumber[index].text = initialData[index].toString()
        }
    }

    suspend fun generate(number: Int) {
        initialData = MutableList(81) { 0 }
        massiveEnterNumber.forEach {
            it.text = ""
            it.setTextColor(blackColor)
        }
        MainScope().launch { makeField(0) }.join()
        openSomeCells(number)
        lastEnterElement?.let { enterNumber(it) }
    }

    suspend fun answer() {
        initialData = emptyList<Int>().toMutableList()
        if (!massiveEnterNumber.any { it.currentTextColor == answerColor }) yourNumbers =
            emptyMap<Int, String>().toMutableMap()
        for ((index, view) in massiveEnterNumber.withIndex()) {
            initialData.add(
                if (view.currentTextColor == blackColor) view.text.toString().toIntOrNull()
                    ?: 0 else 0
            )
            if (view.currentTextColor == enterColor) yourNumbers.put(index, view.text.toString())
        }
        MainScope().launch {
            val result = makeField(0)
            if (!result) {
                val string = "нетрешения"
                val cells = listOf(30, 39, 48, 14, 23, 32, 41, 50, 59, 68)
                massiveEnterNumber.forEach { it.text = "" }
                for ((index, cell) in cells.withIndex()) {
                    massiveEnterNumber[cell].text = string[index].toString()
                }
            } else {
                openSomeCells(81, true)
            }
        }.join()
        lastEnterElement?.let { enterNumber(it) }
    }

    fun clear() {
        val answerMassive =
            massiveEnterNumber.filter { it.currentTextColor == answerColor && it.text != "" }
        val yourMassive =
            massiveEnterNumber.filter { it.currentTextColor == enterColor && it.text != "" }
        if (answerMassive.isNotEmpty()) {
            answerMassive.forEach {
                it.text = ""
                it.setTextColor(blackColor)
            }
            for (num in yourNumbers) {
                massiveEnterNumber[num.key].setTextColor(enterColor)
                massiveEnterNumber[num.key].text = num.value
            }

        } else if (yourMassive.isNotEmpty()) {
            yourMassive.forEach { it.text = "" }
        } else {
            variants.forEach { it.setBackgroundColor(whiteColor) }
            massiveEnterNumber.forEach { it.text = "" }
        }
        lastEnterElement?.let { enterNumber(it) }
    }
}