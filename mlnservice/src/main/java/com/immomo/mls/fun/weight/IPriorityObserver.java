/**
  * Created by MomoLuaNative.
  * Copyright (c) 2019, Momo Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */
package com.immomo.mls.fun.weight;


import android.view.View;

/**
 * Created by Xiong.Fangyu on 2018/11/6
 */
public interface IPriorityObserver {

    void onViewPriorityChanged(View child, int oldPriority, int newPriority);
}