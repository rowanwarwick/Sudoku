package com.example.sudoku

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sudoku.databinding.ActivityMainBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val field by lazy { Field(resources, massiveEnterNumber, variants) }
    private val massiveEnterNumber by lazy {
        listOf(
            binding.x1y1, binding.x1y2, binding.x1y3, binding.x1y4, binding.x1y5, binding.x1y6,
            binding.x1y7, binding.x1y8, binding.x1y9, binding.x2y1, binding.x2y2, binding.x2y3,
            binding.x2y4, binding.x2y5, binding.x2y6, binding.x2y7, binding.x2y8, binding.x2y9,
            binding.x3y1, binding.x3y2, binding.x3y3, binding.x3y4, binding.x3y5, binding.x3y6,
            binding.x3y7, binding.x3y8, binding.x3y9, binding.x4y1, binding.x4y2, binding.x4y3,
            binding.x4y4, binding.x4y5, binding.x4y6, binding.x4y7, binding.x4y8, binding.x4y9,
            binding.x5y1, binding.x5y2, binding.x5y3, binding.x5y4, binding.x5y5, binding.x5y6,
            binding.x5y7, binding.x5y8, binding.x5y9, binding.x6y1, binding.x6y2, binding.x6y3,
            binding.x6y4, binding.x6y5, binding.x6y6, binding.x6y7, binding.x6y8, binding.x6y9,
            binding.x7y1, binding.x7y2, binding.x7y3, binding.x7y4, binding.x7y5, binding.x7y6,
            binding.x7y7, binding.x7y8, binding.x7y9, binding.x8y1, binding.x8y2, binding.x8y3,
            binding.x8y4, binding.x8y5, binding.x8y6, binding.x8y7, binding.x8y8, binding.x8y9,
            binding.x9y1, binding.x9y2, binding.x9y3, binding.x9y4, binding.x9y5, binding.x9y6,
            binding.x9y7, binding.x9y8, binding.x9y9,
        )
    }
    private val variants by lazy {
        listOf(
            binding.num1, binding.num2, binding.num3, binding.num4,
            binding.num5, binding.num6, binding.num7, binding.num8, binding.num9, binding.delete
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        massiveEnterNumber.forEach { view ->
            view.setOnClickListener {
                field.enterNumber(it as TextView)
            }
        }

        variants.forEach { view ->
            view.setOnClickListener {
                field.chooseNumber(it as TextView)
            }
        }

        binding.manual.setOnClickListener {
            field.switchMode(it as Button)
        }

        binding.generate.setOnClickListener {
            MainScope().launch {
                var number = binding.input.text.toString().toIntOrNull()
                if (number == null || number < 10) number = 10
                else if (number > 80) number = 80
                field.generate(number)
            }
        }

        binding.answer.setOnClickListener {
            MainScope().launch {
                field.answer()
            }
        }

        binding.clear.setOnClickListener {
            field.clear()
        }


    }

}