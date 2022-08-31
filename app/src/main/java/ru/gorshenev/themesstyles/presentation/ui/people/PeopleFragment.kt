package ru.gorshenev.themesstyles.presentation.ui.people

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.gorshenev.themesstyles.data.repositories.PeopleDataSource
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.data.Utils
import ru.gorshenev.themesstyles.presentation.base_recycler_view.Adapter
import ru.gorshenev.themesstyles.presentation.base_recycler_view.HolderFactory
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.databinding.FragmentPeopleBinding
import ru.gorshenev.themesstyles.presentation.ui.people.adapter.PeopleHolderFactory
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class PeopleFragment : Fragment(R.layout.fragment_people) {
    private val binding: FragmentPeopleBinding by viewBinding()

    private val holderFactory: HolderFactory = PeopleHolderFactory()
    private val adapter = Adapter<ViewTyped>(holderFactory)
    private var cachedItems: List<ViewTyped> = listOf()
    private val compositeDisposable = CompositeDisposable()
    private val textSubject: PublishSubject<String> = PublishSubject.create()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        loadPeople(30)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    private fun initViews() {
        with(binding) {
            requireActivity().window.statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.colorPrimaryBlack)

            rvPeople.adapter = adapter
            usersField.ivSearch.setOnClickListener {
                adapter.items = emptyList()
                startShimmer()
                loadPeople(Random.nextInt(50))
            }


            usersField.etUsers.addTextChangedListener { text ->
                val queue = text?.toString().orEmpty()
                textSubject.onNext(queue)

                textSubject
                    .distinctUntilChanged()
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .switchMapSingle { searchTxt -> Utils.initUserSearch(cachedItems, searchTxt) }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { filteredPeople -> adapter.items = filteredPeople },
                        { error -> showError(error) }
                    )
                    .apply { compositeDisposable.add(this) }
            }
        }
    }

    private fun loadPeople(count: Int) {
        //todo startWith хз как. + Shimmer изначально сам запускается
        PeopleDataSource.getPeople(count)
            .subscribeOn(Schedulers.io())
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
//            .doOnNext { startShimmer() }
            .subscribe(
                { people ->
                    adapter.items = people
                    cachedItems = people
                },
                { error -> showError(error) },
                {
                    showSuccess()
                    stopShimmer()
                }
            )
            .apply { compositeDisposable.add(this) }
    }

    private fun startShimmer() {
        binding.shimmerPeople.apply {
            visibility = View.VISIBLE
            startShimmer()
        }
    }

    private fun stopShimmer() {
        binding.shimmerPeople.apply {
            visibility = View.GONE
            stopShimmer()
        }
    }

    private fun showError(error: Throwable?) {
        Snackbar.make(binding.root, "Something wrong! $error", Snackbar.LENGTH_LONG).show()
    }

    private fun showSuccess() {
        Snackbar.make(binding.root, "Completed!", Snackbar.LENGTH_SHORT).show()
    }

}