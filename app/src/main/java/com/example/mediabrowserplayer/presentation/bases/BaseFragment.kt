package com.example.mediabrowserplayer.presentation.bases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import com.example.mediabrowserplayer.presentation.viewmodels.MainViewModel

abstract class BaseFragment<VB : ViewBinding> : Fragment(){
    val viewModel : MainViewModel by activityViewModels()

    abstract val bindingInflater: (LayoutInflater) -> VB
    abstract fun viewInitialized()
    private var binding: VB? = null
    val getBinding get() = binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return bindingInflater.invoke(inflater).apply {
            binding = this
            viewInitialized()
        }.root
    }
}