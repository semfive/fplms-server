using System.ComponentModel.DataAnnotations;
using Microsoft.AspNetCore.Mvc.ModelBinding;

namespace DiscussionService.Dtos
{
    public class QueryStringParameters
    {
        private const int MAX_ITEMS_PER_PAGE = 50;
        private int pageSize;
        public string? Question { get; set; }
        public string? Subject { get; set; }

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