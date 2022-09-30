package com.infnet.juarez.avaliacaolimpeza.modelo

import com.android.billingclient.api.SkuDetails

class Produto(var sku : String, var descicao : String?, var preco : String?) {
    var skuDetails : SkuDetails? = null
}