package com.example.gerenciador.graphics.repository;

import com.example.gerenciador.category.dto.CategoryExpenseResponse;
import com.example.gerenciador.products.dto.ProductExpenseResponse;
import com.example.gerenciador.purchase.entity.PurchaseItens;
import com.example.gerenciador.transaction.dto.TransactionMonthResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// pegar valores de gastos por cada categoria

public interface GraphicRepository extends JpaRepository<PurchaseItens, Long> {
    @Query("""
SELECT new com.example.gerenciador.category.dto.CategoryExpenseResponse(
    c.name,
    SUM(i.unitPrice * i.quantity)
)
FROM PurchaseItens i
JOIN i.product p
JOIN p.category c
JOIN i.purchase pur
WHERE pur.family.id = :familyId
AND pur.purchaseStatus = com.example.gerenciador.purchase.entity.PurchaseStatus.CLOSED
GROUP BY c.name
ORDER BY SUM(i.unitPrice * i.quantity) DESC
""")
    List<CategoryExpenseResponse> getExpensesByCategory(Long familyId);


// pegar valores de gastos por cada categoria filtrnado por ano e mes

    @Query("""
SELECT new com.example.gerenciador.category.dto.CategoryExpenseResponse(
    c.name,
    SUM(i.unitPrice * i.quantity)
)
FROM PurchaseItens i
JOIN i.product p
JOIN p.category c
JOIN i.purchase pur
WHERE pur.family.id = :familyId
AND pur.purchaseStatus = com.example.gerenciador.purchase.entity.PurchaseStatus.CLOSED
AND FUNCTION('YEAR', pur.dateTime) = :year
AND FUNCTION('MONTH', pur.dateTime) = :month
GROUP BY c.name
ORDER BY SUM(i.unitPrice * i.quantity) DESC
""")
    List<CategoryExpenseResponse> getCategoryExpensesByMonth(Long familyId, Integer year, Integer month);




    // pegar valores de todas as trnasações por mes
    @Query("""
SELECT new com.example.gerenciador.transaction.dto.TransactionMonthResponse(
    month(t.dateTime),
    SUM(t.amount)
)
FROM Transaction t
WHERE t.family.id = :familyId
AND t.transactionType = com.example.gerenciador.transaction.entity.TransactionType.EXPENSE
AND year(t.dateTime) = :year
GROUP BY month(t.dateTime)
ORDER BY month(t.dateTime)
""")
    List<TransactionMonthResponse> getMonthlyExpenses(Long familyId, Integer year);



    // pegar valores totais dos produtos para o gráfico
    @Query("""
SELECT new com.example.gerenciador.products.dto.ProductExpenseResponse(
    p.name,
    SUM(i.unitPrice * i.quantity)
)
FROM PurchaseItens i
JOIN i.product p
JOIN i.purchase pur
WHERE pur.family.id = :familyId
AND pur.purchaseStatus = com.example.gerenciador.purchase.entity.PurchaseStatus.CLOSED
AND FUNCTION('YEAR', pur.dateTime) = :year
AND FUNCTION('MONTH', pur.dateTime) = :month
GROUP BY p.name
ORDER BY SUM(i.unitPrice * i.quantity) DESC
""")
    List<ProductExpenseResponse> getProductExpensesByMonth(
            Long familyId,
            Integer year,
            Integer month
    );

}
