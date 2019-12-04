package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlin.random.Random


class GameViewModel : ViewModel() {

    companion object {
        //Time when the game is over
        private const val DONE = 0L

        //countdown time interval
        private const val ONE_SECOND = 100L

        // Total time for the game
        private const val COUNT_DOWN_TIME = 60000L
    }

    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime

    //The string version of the current time
    val currentTimeString = Transformations.map(currentTime){
        time -> DateUtils.formatElapsedTime(time)
    }

    private val timer: CountDownTimer

    // The current word
    private val _word = MutableLiveData<String>()
    val word: LiveData<String>
        get() = _word

    //The lenght of the current word for hint
    val currentWordLength = Transformations.map(word){
        currentWord -> currentWord.length
    }
    //The position of the randomly shown character for hint
    val position = Transformations.map(word){
        currentWord -> Random.nextInt(currentWord.length)
    }

    //The position of a random letter in the word
    val characterString = Transformations.map(word){
        currentWord -> currentWord[position.value!!].toString()
    }

    // The current score
    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    //Event which triggers the end of the game
    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish


    init {
        Log.i("GameViewModel", "GameViewModel created")
        _word.value = ""
        _score.value = 0
        resetList()
        nextWord()

        timer = object : CountDownTimer(COUNT_DOWN_TIME, ONE_SECOND){
            override fun onFinish() {
                _currentTime.value = DONE
                onGameFinished()
            }

            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = millisUntilFinished/ ONE_SECOND
            }
        }

        timer.start()
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        if (wordList.isEmpty()) {
            resetList()
        } else {
            //Select and remove a word from the list
            _word.value = wordList.removeAt(0)
        }
    }

    /** Methods for buttons presses **/

    fun onSkip() {
        if (!wordList.isEmpty()) {
            _score.value = (score.value)?.minus(1)
        }
        nextWord()
    }

    fun onCorrect() {
        if (!wordList.isEmpty()) {
            _score.value = (score.value)?.plus(1)
        }
        nextWord()
    }

    fun onGameFinished() {
        _eventGameFinish.value = true
    }

    fun onGameFinishComplete() {
        _eventGameFinish.value = false
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel", "GameViewModel destroyed")
        timer.cancel()
    }


}