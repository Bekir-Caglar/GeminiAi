package com.bekircaglar.geminiai.ui

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bekircaglar.geminiai.databinding.FragmentAskTextBinding
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private lateinit var binding: FragmentAskTextBinding
class AskTextFragment : Fragment() {


    // Get your API key from https://makersuite.google.com/app/apikey
    private val API_KEY = "YOUR_API_KEY"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAskTextBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         val generativeModel = GenerativeModel("gemini-pro",API_KEY)
        binding.answerText.setMovementMethod(ScrollingMovementMethod())

        binding.goButton.setOnClickListener {
            val job = CoroutineScope(Dispatchers.IO).launch {
                val prompt = binding.askEditText.text.toString()
                val response = generativeModel.generateContent(prompt)
                binding.answerText.text = response.text

            }
        }
    }





}