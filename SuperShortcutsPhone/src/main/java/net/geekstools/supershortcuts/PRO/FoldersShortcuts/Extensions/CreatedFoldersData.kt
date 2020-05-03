/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/3/20 7:39 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.FoldersShortcuts.Extensions

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.coroutines.*
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.Adapters.FolderShortcutsAdapter
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.FolderShortcuts
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.AdapterItemsData.AdapterItemsData
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScroller.Factory.IndexedFastScrollerFactory
import net.geekstools.supershortcuts.PRO.Utils.UI.PopupIndexedFastScroller.IndexedFastScroller
import java.util.*
import kotlin.collections.ArrayList

fun FolderShortcuts.loadCreatedFoldersData()  = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
    createdFolderListItem.clear()

    val listOfNewCharOfItemsForIndex: ArrayList<String> = ArrayList<String>()

    if (!getFileStreamPath(FolderShortcuts.FolderShortcutsFile).exists()) {

        createdFolderListItem.add(AdapterItemsData(packageName, arrayOf(packageName)))

    } else {

        val folderNameList = functionsClass.readFileLine(FolderShortcuts.FolderShortcutsFile)
        folderNameList.sort()
        folderNameList.forEachIndexed { index, folderName ->

            createdFolderListItem.add(AdapterItemsData(
                    folderName,
                    functionsClass.readFileLine(folderName)
            ))

            listOfNewCharOfItemsForIndex.add(folderName.substring(0, 1).toUpperCase(Locale.getDefault()))

        }

        createdFolderListItem.add(AdapterItemsData(packageName, arrayOf(packageName)))
    }

    withContext(Dispatchers.Main) {

        folderSelectionListAdapter = FolderShortcutsAdapter(this@loadCreatedFoldersData,
                createdFolderListItem)

        folderShortcutsViewBinding.recyclerViewList.adapter = folderSelectionListAdapter

        folderShortcutsViewBinding.loadingSplash.visibility = View.INVISIBLE

        if (!resetAdapter) {
            val animationFadeOut = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out)
            folderShortcutsViewBinding.loadingSplash.startAnimation(animationFadeOut)
        }

        folderShortcutsViewBinding.confirmButton.visibility = View.VISIBLE

        val animationFadeIn = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in)
        folderShortcutsViewBinding.selectedShortcutCounterView.startAnimation(animationFadeIn)
        animationFadeIn.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {

                folderShortcutsViewBinding.selectedShortcutCounterView.text = functionsClass.countLineInnerFile(FolderShortcuts.FolderShortcutsSelectedFile).toString()
            }

            override fun onAnimationEnd(animation: Animation) {

                folderShortcutsViewBinding.selectedShortcutCounterView.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        resetAdapter = false

        PublicVariable.advanceShortcutsMaxAppShortcutsCounter = functionsClass.countLine(".categorySuperSelected");
        resetAdapter = false;
    }

    /*Indexed Popup Fast Scroller*/
    val indexedFastScroller: IndexedFastScroller = IndexedFastScroller(
            context = applicationContext,
            layoutInflater = layoutInflater,
            rootView = folderShortcutsViewBinding.MainView,
            nestedScrollView = folderShortcutsViewBinding.nestedScrollView,
            recyclerView = folderShortcutsViewBinding.recyclerViewList,
            fastScrollerIndexViewBinding = folderShortcutsViewBinding.fastScrollerIndexInclude,
            indexedFastScrollerFactory = IndexedFastScrollerFactory(
                    popupEnable = true,
                    popupTextColor = getColor(R.color.light),
                    indexItemTextColor = getColor(R.color.dark))
    )
    indexedFastScroller.initializeIndexView().await()
            .loadIndexData(listOfNewCharOfItemsForIndex = listOfNewCharOfItemsForIndex).await()
    /*Indexed Popup Fast Scroller*/
}