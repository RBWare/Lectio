package me.ash.reader.unreddit.ui.backup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import me.ash.reader.R
import me.ash.reader.unreddit.data.model.BackupTypeItem
import me.ash.reader.unreddit.data.model.backup.BackupType
import me.ash.reader.databinding.FragmentBackupChoiceBinding
import me.ash.reader.unreddit.ui.base.BaseFragment
import me.ash.reader.unreddit.util.extension.applyWindowInsets
import me.ash.reader.unreddit.util.extension.launchRepeat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BackupChoiceFragment : BaseFragment() {

    private var _binding: FragmentBackupChoiceBinding? = null
    private val binding get() = _binding!!

    private val backupViewModel: BackupViewModel by hiltNavGraphViewModels(R.id.backup)

    private lateinit var backupChoiceAdapter: BackupChoiceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBackupChoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        bindViewModel()
    }

    private fun initRecyclerView() {
        backupChoiceAdapter = BackupChoiceAdapter(this::selectType).apply {
            submitList(BACKUP_TYPES)
        }

        binding.listBackupTypes.apply {
            applyWindowInsets(left = false, top = false, right = false)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = backupChoiceAdapter
        }
    }

    private fun bindViewModel() {
        launchRepeat(Lifecycle.State.STARTED) {
            launch {
                backupViewModel.backupType.collect { backupType ->
                    backupChoiceAdapter.setChecked(backupType)
                }
            }
        }
    }

    private fun selectType(backupType: BackupType) {
        if (backupViewModel.backupType.value != backupType) {
            // Reset Uri on type change
            backupViewModel.setUri(null)
        }
        backupViewModel.setBackupType(backupType)
    }

    override fun onBackPressed() {
        // Disabled
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val BACKUP_TYPES = BackupType.values().map { BackupTypeItem(it, false) }
    }
}
