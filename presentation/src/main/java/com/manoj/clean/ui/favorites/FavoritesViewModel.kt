package com.manoj.clean.ui.favorites

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.manoj.clean.ui.common.base.BaseViewModel
import com.manoj.clean.util.SingleRequestStateFlow
import com.manoj.clean.util.singleSharedFlow
import com.manoj.data.util.DispatchersProvider
import com.manoj.domain.entities.MovieDetails
import com.manoj.domain.entities.UiState
import com.manoj.domain.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    dispatchers: DispatchersProvider
) : BaseViewModel(dispatchers) {


    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<MovieDetails> = singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()
    val list: SingleRequestStateFlow<List<List<FeedItem>>> = SingleRequestStateFlow()
    val mediaList: ArrayList<ArrayList<FeedItem>> = arrayListOf()
    fun onLoadStateUpdate(loadState: CombinedLoadStates, itemCount: Int) {
        val showLoading = loadState.refresh is LoadState.Loading
        val showNoData = loadState.append.endOfPaginationReached && itemCount < 1

        _uiState.update {
            it.copy(
                showLoading = showLoading, errorMessage = showNoData.toString()
            )
        }
    }

    fun getData() = launchOnIO {
        list.setValue(State.loading())
        val data = listOf(
            listOf(
                FeedItem(
                    "https://user-images.githubusercontent.com/90382113/170887700-e405c71e-fe31-458d-8572-aea2e801eecc.mp4",
                    "video",
                    "https://image.tmdb.org/t/p/w342/7lTnXOy0iNtBAdRP3TZvaKJ77F6.jpg",
                    "1282:718"
                ),
                FeedItem(
                    "https://user-images.githubusercontent.com/90382113/170887700-e405c71e-fe31-458d-8572-aea2e801eecc.mp4",
                    "video",
                    "https://image.tmdb.org/t/p/w342/qhb1qOilapbapxWQn9jtRCMwXJF.jpg",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    "video",
                    "https://image.tmdb.org/t/p/w342/ldfCF9RhR40mppkzmftxapaHeTo.jpg",
                    "1282:718"
                ),
                FeedItem(
                    "https://i.imgur.com/CzXTtJV.jpg",
                    "image",
                    "https://i.imgur.com/CzXTtJV.jpg",
                    "604:450"
                )
            ),
            listOf(
                FeedItem(
                    "https://user-images.githubusercontent.com/90382113/170885742-d82e3b59-e45a-4fcf-a851-fed58ff5a690.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                ),
                FeedItem(
                    "https://image.tmdb.org/t/p/w342/eSatbygYZp8ooprBHZdb6GFZxGB.jpg",
                    "image",
                    "https://i.imgur.com/CzXTtJV.jpg",
                    "604:450"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                ),
                FeedItem(
                    "https://user-images.githubusercontent.com/90382113/170889265-7ed9a56c-dd5f-4d78-b453-18b011644da0.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                ),
                FeedItem(
                    "https://user-images.githubusercontent.com/90382113/170890384-43214cc8-79c6-4815-bcb7-e22f6f7fe1bc.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                ),
                FeedItem(
                    "https://image.tmdb.org/t/p/w342/eSatbygYZp8ooprBHZdb6GFZxGB.jpg",
                    "image",
                    "https://i.imgur.com/CzXTtJV.jpg",
                    "604:450"
                )

            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                ),
                FeedItem(
                    "https://image.tmdb.org/t/p/w342/eSatbygYZp8ooprBHZdb6GFZxGB.jpg",
                    "image",
                    "https://i.imgur.com/CzXTtJV.jpg",
                    "604:450"
                ),
                FeedItem(
                    "https://image.tmdb.org/t/p/w342/eSatbygYZp8ooprBHZdb6GFZxGB.jpg",
                    "image",
                    "https://i.imgur.com/CzXTtJV.jpg",
                    "604:450"
                ),
                FeedItem(
                    "https://image.tmdb.org/t/p/w342/eSatbygYZp8ooprBHZdb6GFZxGB.jpg",
                    "image",
                    "https://i.imgur.com/CzXTtJV.jpg",
                    "604:450"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            ),
            listOf(
                FeedItem(
                    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    "video",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Big_Buck_Bunny_thumbnail_vlc.png/1200px-Big_Buck_Bunny_thumbnail_vlc.png",
                    "1282:718"
                )
            )
        )
        list.setValue(State.success(data))
    }


}
