using System.ComponentModel.DataAnnotations;
using Microsoft.AspNetCore.Mvc.ModelBinding;
using DiscussionService.Contracts;

namespace DiscussionService.Dtos
{
    public class AnswersQueryStringParameters : IQueryStringParameters
    {
        private const int MAX_ITEMS_PER_PAGE = 50;
        private int pageSize;

        [BindRequired]
        public int PageNumber { get; set; } = 1;

        [BindRequired]
        public int PageSize
        {
            get => pageSize;
            set => pageSize = value > MAX_ITEMS_PER_PAGE ? MAX_ITEMS_PER_PAGE : value;
        }

    }
}