namespace DiscussionService.Contracts
{
    public interface IQueryStringParameters
    {
        public int PageNumber { get; set; }
        public int PageSize { get; set; }
    }
}