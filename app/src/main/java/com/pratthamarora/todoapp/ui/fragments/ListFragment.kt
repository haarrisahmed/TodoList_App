package com.pratthamarora.todoapp.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pratthamarora.todoapp.R
import com.pratthamarora.todoapp.databinding.FragmentListBinding
import com.pratthamarora.todoapp.ui.adapter.TodoAdapter
import com.pratthamarora.todoapp.utils.SwipeToDelete
import com.pratthamarora.todoapp.viewmodel.SharedViewModel
import com.pratthamarora.todoapp.viewmodel.ToDoViewModel

class ListFragment : Fragment() {

    private val viewModel by viewModels<ToDoViewModel>()
    private val sharedViewModel by viewModels<SharedViewModel>()
    private val todoAdapter by lazy { TodoAdapter(arrayListOf()) }
    private var _binding: FragmentListBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel
        setupRecyclerView()
        observeViewModel()

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun observeViewModel() {
        viewModel.getAllData.observe(viewLifecycleOwner, Observer {
            sharedViewModel.checkEmptyDB(it)
            todoAdapter.setList(it)
        })

    }

    private fun setupRecyclerView() {
        binding.listRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = todoAdapter
            swipeToDelete(this)
        }
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = todoAdapter.todoList.asReversed()[viewHolder.adapterPosition]
                viewModel.deleteData(item)
                Toast.makeText(
                    requireContext(),
                    "Successfully Deleted ${item.title}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteAll -> {
                deleteAllData()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllData() {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteAllData()
                Toast.makeText(
                    requireContext(),
                    "Successfully Deleted All ToDos!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("No") { _, _ -> }
            .setTitle("Delete All ToDos?")
            .setMessage("Are you sure you want to delete everything?")
            .create()

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}