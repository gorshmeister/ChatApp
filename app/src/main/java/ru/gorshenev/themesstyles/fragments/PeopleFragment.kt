package ru.gorshenev.themesstyles.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.gorshenev.themesstyles.PeopleDataSource
import ru.gorshenev.themesstyles.Presenter
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.Utils.initUserSearchObservable
import ru.gorshenev.themesstyles.baseRecyclerView.Adapter
import ru.gorshenev.themesstyles.baseRecyclerView.HolderFactory
import ru.gorshenev.themesstyles.baseRecyclerView.ViewTyped
import ru.gorshenev.themesstyles.databinding.FragmentPeopleBinding
import ru.gorshenev.themesstyles.holderFactory.PeopleHolderFactory
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class PeopleFragment : Fragment(R.layout.fragment_people) {
    private val binding: FragmentPeopleBinding by viewBinding()

    private val holderFactory: HolderFactory = PeopleHolderFactory()
    private val adapter = Adapter<ViewTyped>(holderFactory)
    private var cachedItems: List<ViewTyped> = listOf()
    private val compositeDisposable = CompositeDisposable()
    private val presenter: Presenter = Presenter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.colorPrimaryBlack)

        initViews()
        loadPeople(30)
    }

    override fun onResume() {
        super.onResume()
//        binding.shimmerPeople.startShimmer()
    }

    override fun onPause() {
        super.onPause()
        binding.shimmerPeople.stopShimmer()
    }


    private fun initViews() {
        with(binding) {
            rvPeople.adapter = adapter
            usersField.ivSearch.setOnClickListener {
                adapter.items = emptyList()
                binding.shimmerPeople.apply {
                    visibility = View.VISIBLE
                    startShimmer()
                }
                loadPeople(Random.nextInt(50))
            }


            usersField.etUsers.addTextChangedListener { text ->
                Observable.create<String> { emitter ->
                    emitter.onNext(text.toString())
                }
                    //todo надо тут фильтроваь его или нет, если я тут фильтрую, то как обработать в самой функции
//                    .filter { searchTxt -> searchTxt.isNotEmpty() }
                    .distinctUntilChanged()
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .switchMap { searchTxt -> initUserSearchObservable(cachedItems, searchTxt) }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { filteredPeople -> adapter.items = filteredPeople }
                    .apply { compositeDisposable.add(this) }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun loadPeople(count: Int) {
        PeopleDataSource.getPeopleObservable(count)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { binding.shimmerPeople.startShimmer() }
            .subscribe(
                { people ->
                    adapter.items = people
                    cachedItems = people
                },
                { error -> showError(error) },
                {
                    Snackbar.make(binding.root, "Completed", Snackbar.LENGTH_SHORT).show()
                    binding.shimmerPeople.apply {
                        stopShimmer()
                        visibility = View.GONE
                    }
                }
            )
            .apply { compositeDisposable.add(this) }
    }

    private fun showError(error: Throwable?) {
        Snackbar.make(binding.root, "Something wrong! $error", Snackbar.LENGTH_SHORT).show()
    }

}